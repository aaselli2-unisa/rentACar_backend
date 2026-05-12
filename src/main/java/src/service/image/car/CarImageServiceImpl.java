package src.service.image.car;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import src.core.exception.DataNotFoundException;
import src.core.exception.FileException;
import src.core.utilities.ImageUtils;
import src.repository.image.CarImageEntity;
import src.repository.image.CarImageRepository;
import src.service.external.CloudinaryServiceImpl;
import src.service.image.ImageRules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static src.core.exception.type.FileExceptionType.INVALID_FILE_TYPE;
import static src.core.exception.type.FileExceptionType.PHOTO_DELETE_FAILED;
import static src.core.exception.type.FileExceptionType.PHOTO_UPLOAD_FAILED;
import static src.core.exception.type.FileExceptionType.PHOTO_IS_EMPTY;
import static src.core.exception.type.NotFoundExceptionType.IMAGE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CarImageServiceImpl implements CarImageService {

    private static final Logger log = LoggerFactory.getLogger(CarImageServiceImpl.class);

    // Security patch V07: only JPEG, PNG and WebP content-types are accepted.
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    // Security patch V12: magic byte signatures for supported image formats.
    // Prevents Content-Type spoofing: a client declaring image/jpeg but sending PHP bytes
    // would pass the Content-Type whitelist but fail the magic-byte check below.
    private static final byte[] JPEG_MAGIC = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_MAGIC  = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] WEBP_RIFF  = {0x52, 0x49, 0x46, 0x46}; // "RIFF"
    private static final byte[] WEBP_MARK  = {0x57, 0x45, 0x42, 0x50}; // "WEBP" at offset 8

    private final CarImageRepository repository;
    private final ImageRules rules;
    private final CloudinaryServiceImpl cloudinaryServiceImpl;

    @Override
    public CarImageEntity create(MultipartFile file, String licensePlate) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new FileException(PHOTO_IS_EMPTY);
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new FileException(INVALID_FILE_TYPE);
        }
        // Security patch V12: verify file magic bytes match the declared Content-Type.
        // Prevents spoofed Content-Type from bypassing the whitelist above.
        byte[] fileBytes = file.getBytes();
        checkMagicBytes(fileBytes);
        try {
            byte[] newByte = ImageUtils.resizeImage(fileBytes, 1920, 1080);
            String imageUrl = cloudinaryServiceImpl.uploadFileCar(file, licensePlate);
            byte[] decompressedData = ImageUtils.decompressImage(newByte);
            return repository.save(CarImageEntity.carImageBuilder()
                    .name(licensePlate)
                    .type(file.getContentType())
                    .url(imageUrl)
                    .imageData(ImageUtils.compressImage(decompressedData))
                    .build()
            );
        } catch (Exception e) {
            log.error("Car image upload failed for '{}'", licensePlate, e);
            throw new FileException(PHOTO_UPLOAD_FAILED);
        }
    }


    @Override
    public byte[] downloadImage(String fileName) {
        CarImageEntity dbImageData = repository.findByName(fileName).orElse(null);
        assert dbImageData != null;
        return ImageUtils.decompressImage(dbImageData.getImageData());
    }

    @Override
    public CarImageEntity getById(int id) {
        return repository.findById(id).orElseThrow(() -> new DataNotFoundException(IMAGE_NOT_FOUND));
    }

    @Override
    public List<CarImageEntity> getAll() {
        List<CarImageEntity> images = repository.findAll();
        rules.checkDataList(images);
        return images;
    }

    private void checkMagicBytes(byte[] bytes) {
        if (bytes == null || bytes.length < 8) {
            throw new FileException(INVALID_FILE_TYPE);
        }
        if (hasPrefix(bytes, JPEG_MAGIC)) return;
        if (hasPrefix(bytes, PNG_MAGIC)) return;
        if (hasPrefix(bytes, WEBP_RIFF) && bytes.length >= 12 && hasPrefixAt(bytes, WEBP_MARK, 8)) return;
        throw new FileException(INVALID_FILE_TYPE);
    }

    private static boolean hasPrefix(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) return false;
        }
        return true;
    }

    private static boolean hasPrefixAt(byte[] data, byte[] prefix, int offset) {
        if (data.length < offset + prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (data[offset + i] != prefix[i]) return false;
        }
        return true;
    }

    @Override
    public void delete(int id) throws IOException {
        CarImageEntity carImage = this.getById(id);
        try {
            if (cloudinaryServiceImpl.deleteFile(carImage.getName())) {
                repository.delete(this.getById(id));
            }
        } catch (Exception e) {
            throw new FileException(PHOTO_DELETE_FAILED);
        }
    }
}