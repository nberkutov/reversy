package dto.response;

import exception.GameErrorCode;
import exception.ServerException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
