package dto.response;

import controllers.commands.CommandResponse;
import exception.GameErrorCode;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ErrorResponse extends GameResponse {
    private GameErrorCode errorCode;
    private String message;

    public static ErrorResponse toDto(GameException exception) {
        return new ErrorResponse(exception.getErrorCode(), exception.getMessage());
    }
}
