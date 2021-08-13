package dto.request.player;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class AuthPlayerRequest extends CreatePlayerRequest {

    public AuthPlayerRequest(final String nickname) {
        super(nickname);
    }
}
