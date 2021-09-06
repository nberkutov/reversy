package dto.request.player;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class AuthPlayerRequest extends CreatePlayerRequest {

    public AuthPlayerRequest(String nickname) {
        super(nickname);
    }
}
