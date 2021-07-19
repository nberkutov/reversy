package client;

import dto.response.*;
import dto.response.player.CreatePlayerResponse;
import exception.GameException;
import services.JsonService;

import java.io.IOException;

public class ClientController {

    public static void actionByResponseFromServer(final GameResponse gameResponse) throws GameException, IOException, InterruptedException {

        switch (JsonService.getCommandByResponse(gameResponse)) {
            case ERROR:
                ErrorResponse error = (ErrorResponse) gameResponse;
//                actionError(error);
                break;
            case GAME_PLAYING:
                GameBoardResponse response = (GameBoardResponse) gameResponse;
//                actionPlaying(response);
                break;
            case CREATE_PLAYER:
                CreatePlayerResponse createPlayer = (CreatePlayerResponse) gameResponse;
//                actionCreatePlayer(createPlayer);
                break;
            case GAME_START:
                SearchGameResponse createGame = (SearchGameResponse) gameResponse;
//                actionStartGame(createGame);
                break;
            case MESSAGE:
                MessageResponse message = (MessageResponse) gameResponse;
//                actionMessage(message);
                break;
            default:
//                log.error("Unknown response {}", gameResponse);
        }
    }


}
