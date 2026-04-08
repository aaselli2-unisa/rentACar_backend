package src.core.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileExceptionType {

    PHOTO_UPLOAD_FAILED(6000, "Photo upload failed"),
    PHOTO_DELETE_FAILED(6001, "Photo deletion failed"),
    PHOTO_IS_EMPTY(6002, "Photo file is missing or empty");
    //------------------------------------------------------------------
    private final Integer errorCode;
    private final String message;
}
