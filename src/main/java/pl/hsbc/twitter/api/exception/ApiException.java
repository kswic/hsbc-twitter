package pl.hsbc.twitter.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

    private static final String MSG_API_ERROR = "API internal error";
    private final HttpStatus httpStatus;

    public ApiException() {
        super(MSG_API_ERROR);
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ApiException(String message) {
        super(message);
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ApiException(Throwable cause) {
        super(MSG_API_ERROR, cause);
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
