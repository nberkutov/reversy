package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class ErrorResponse implements GameResponse {
    private final GameErrorCode errorCode;
    private final String message;

    public static ErrorResponse toDto(final ServerException exception) {
        return new ErrorResponse(exception.getErrorCode(), exception.getMessage());
    }
}
