package dto.response;

import exception.GameErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse extends GameResponse {
    private GameErrorCode errorCode;
    private String message;
}
