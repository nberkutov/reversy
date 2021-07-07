package exception;

import lombok.Data;

@Data
public class GameException extends Exception {
    private GameErrorCode errorCode;

    public GameException(GameErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public GameException(String message, GameErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
