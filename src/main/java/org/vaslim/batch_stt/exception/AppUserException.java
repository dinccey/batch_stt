package org.vaslim.batch_stt.exception;

import org.springframework.security.core.AuthenticationException;

public class AppUserException extends AuthenticationException {
    public AppUserException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AppUserException(String msg) {
        super(msg);
    }
}
