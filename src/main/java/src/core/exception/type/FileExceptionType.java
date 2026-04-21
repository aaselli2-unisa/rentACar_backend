package src.core.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileExceptionType {

    PHOTO_UPLOAD_FAILED(6000, "Photo upload failed"),
    PHOTO_DELETE_FAILED(6001, "Photo deletion failed"),
    PHOTO_IS_EMPTY(6002, "Photo file is missing or empty"),
    // Security patch V07: only JPEG, PNG, WebP are accepted.
    INVALID_FILE_TYPE(6003, "Unsupported file type. Only JPEG, PNG and WebP images are allowed.");
    //------------------------------------------------------------------
    private final Integer errorCode;
    private final String message;
}
