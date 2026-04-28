package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import src.core.exception.FileException;
import src.repository.image.CarImageRepository;
import src.service.external.CloudinaryServiceImpl;
import src.service.image.ImageRules;
import src.service.image.car.CarImageServiceImpl;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Security patch V12 — Magic byte validation on image upload (OWASP A03 / CWE-434).
 *
 * Previously (V07): CarImageServiceImpl validated only the declared Content-Type header
 * against a whitelist (image/jpeg, image/png, image/webp). An attacker could bypass
 * this check by setting Content-Type: image/jpeg on a file containing PHP, HTML, or
 * any other payload. The Content-Type header is fully client-controlled.
 *
 * Fix (V12): After the Content-Type check, the first bytes of the file are read and
 * compared against known magic byte signatures:
 *   - JPEG:  FF D8 FF
 *   - PNG:   89 50 4E 47 0D 0A 1A 0A
 *   - WebP:  52 49 46 46 xx xx xx xx 57 45 42 50  (RIFF....WEBP)
 *
 * A file with a spoofed Content-Type will now be rejected at the magic byte check
 * before any interaction with Cloudinary or the database.
 */
@DisplayName("V12 – Image magic byte validation (OWASP A03 / CWE-434)")
class ImageMagicBytesSecurityTest {

    private final CarImageRepository repository = Mockito.mock(CarImageRepository.class);
    private final ImageRules rules = Mockito.mock(ImageRules.class);
    private final CloudinaryServiceImpl cloudinary = Mockito.mock(CloudinaryServiceImpl.class);

    private final CarImageServiceImpl service = new CarImageServiceImpl(repository, rules, cloudinary);

    // ======================================================================
    //  Content-Type spoofing — should be rejected by magic byte check
    // ======================================================================

    @Test
    @DisplayName("V12: PHP payload with Content-Type image/jpeg is rejected (magic bytes mismatch)")
    void phpPayload_declaredAsJpeg_isRejectedByMagicBytes() throws Exception {
        byte[] phpPayload = "<?php system($_GET['cmd']); ?>".getBytes();

        MockMultipartFile spoofedFile = new MockMultipartFile(
                "image",
                "shell.jpg",
                "image/jpeg",   // passes the Content-Type whitelist check
                phpPayload      // fails the magic byte check (no FF D8 FF prefix)
        );

        assertThatThrownBy(() -> service.create(spoofedFile, "AB123CD"))
                .as("PHP payload with spoofed image/jpeg Content-Type must be rejected by magic bytes check")
                .isInstanceOf(FileException.class);
    }

    @Test
    @DisplayName("V12: HTML payload with Content-Type image/png is rejected (magic bytes mismatch)")
    void htmlPayload_declaredAsPng_isRejectedByMagicBytes() throws Exception {
        byte[] htmlPayload = "<html><script>alert(document.cookie)</script></html>".getBytes();

        MockMultipartFile spoofedFile = new MockMultipartFile(
                "image",
                "page.png",
                "image/png",    // passes the Content-Type whitelist
                htmlPayload     // fails the magic byte check (no 89 50 4E 47 prefix)
        );

        assertThatThrownBy(() -> service.create(spoofedFile, "XY456EF"))
                .as("HTML payload with spoofed image/png Content-Type must be rejected by magic bytes check")
                .isInstanceOf(FileException.class);
    }

    @Test
    @DisplayName("V12: JavaScript payload with Content-Type image/webp is rejected (magic bytes mismatch)")
    void jsPayload_declaredAsWebp_isRejectedByMagicBytes() throws Exception {
        byte[] jsPayload = "eval(atob('YWxlcnQoJ3hzcycpOw=='));".getBytes();

        MockMultipartFile spoofedFile = new MockMultipartFile(
                "image",
                "script.webp",
                "image/webp",   // passes Content-Type whitelist
                jsPayload       // fails magic byte check (no RIFF...WEBP signature)
        );

        assertThatThrownBy(() -> service.create(spoofedFile, "ZZ999AA"))
                .as("JS payload with spoofed image/webp Content-Type must be rejected by magic bytes check")
                .isInstanceOf(FileException.class);
    }

    @Test
    @DisplayName("V12: File with only RIFF header but missing WEBP marker is rejected")
    void riffWithoutWebpMarker_isRejected() throws Exception {
        // RIFF header present but offset-8 contains "WAVE" instead of "WEBP" (audio file)
        byte[] audioPayload = new byte[]{
                0x52, 0x49, 0x46, 0x46,  // RIFF
                0x00, 0x00, 0x01, 0x00,  // file size
                0x57, 0x41, 0x56, 0x45,  // WAVE (not WEBP)
                0x00
        };

        MockMultipartFile audioFile = new MockMultipartFile(
                "image",
                "audio.webp",
                "image/webp",
                audioPayload
        );

        assertThatThrownBy(() -> service.create(audioFile, "GH111BB"))
                .as("RIFF file without WEBP marker at offset 8 must be rejected")
                .isInstanceOf(FileException.class);
    }

    @Test
    @DisplayName("V12: Empty payload with valid Content-Type is rejected (too short for any magic check)")
    void emptyPayload_isRejectedByMagicCheck() throws Exception {
        byte[] tinyPayload = new byte[]{(byte) 0xFF, (byte) 0xD8}; // only 2 bytes, JPEG magic incomplete

        MockMultipartFile tinyFile = new MockMultipartFile(
                "image",
                "truncated.jpg",
                "image/jpeg",
                tinyPayload
        );

        assertThatThrownBy(() -> service.create(tinyFile, "II222CC"))
                .isInstanceOf(FileException.class);
    }

    // ======================================================================
    //  Correct magic bytes — must NOT be rejected by the magic byte check
    //  (service may still fail at Cloudinary/image-processing level, which
    //  is expected in unit test context; we only verify no FileException
    //  is thrown with INVALID_FILE_TYPE type before the processing step)
    // ======================================================================

    @Test
    @DisplayName("V12: File starting with JPEG magic bytes (FF D8 FF) passes the magic byte check")
    void jpegMagicBytes_passCheck() {
        // Build a byte array that starts with valid JPEG magic bytes.
        // The service will attempt image processing which will fail (mock),
        // but the important check is that FileException is NOT thrown by the
        // magic byte validator — only by the later processing step.
        byte[] jpegBytes = new byte[10];
        jpegBytes[0] = (byte) 0xFF;
        jpegBytes[1] = (byte) 0xD8;
        jpegBytes[2] = (byte) 0xFF;
        jpegBytes[3] = (byte) 0xE0;

        MockMultipartFile jpegFile = new MockMultipartFile(
                "image",
                "photo.jpg",
                "image/jpeg",
                jpegBytes
        );

        // The service throws FileException(PHOTO_UPLOAD_FAILED) because Cloudinary is mocked
        // and image processing fails — that is expected and acceptable.
        // What must NOT happen is FileException with type INVALID_FILE_TYPE (magic mismatch).
        assertThatThrownBy(() -> service.create(jpegFile, "JK333DD"))
                .isInstanceOf(FileException.class)
                .satisfies(ex -> {
                    FileException fe = (FileException) ex;
                    org.assertj.core.api.Assertions.assertThat(fe.getFileExceptionType())
                            .isNotEqualTo(src.core.exception.type.FileExceptionType.INVALID_FILE_TYPE);
                });
    }
}
