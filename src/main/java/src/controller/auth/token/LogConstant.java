package src.controller.auth.token;

import static src.controller.AnsiColorConstant.*;

public final class LogConstant {

    // Log Messages
    // Security patch V05: token value removed — use log.info(REFRESH_TOKEN_REQUEST_RECEIVED) without args.
    public static final String REFRESH_TOKEN_REQUEST_RECEIVED = ANSI_BLUE + "Received refresh token request." + ANSI_RESET;
    public static final String REFRESH_TOKEN_SUCCESSFUL = ANSI_GREEN + "Token refresh successful." + ANSI_RESET;
}
