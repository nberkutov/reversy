package dto.response;

import exception.GameErrorCode;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class ErrorResponse extends GameResponse {
    private GameErrorCode errorCode;
    private String message;

    public static ErrorResponse toDto(GameException exception) {
        return new ErrorResponse(exception.getErrorCode(), exception.getMessage());
    }
}
