package dto.response;

import controllers.commands.CommandResponse;
import exception.GameErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static services.BaseService.GSON;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse extends GameResponse {
    private GameErrorCode errorCode;

}
