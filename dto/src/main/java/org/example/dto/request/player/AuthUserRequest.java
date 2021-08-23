package org.example.dto.request.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.dto.request.GameRequest;

@Data
@AllArgsConstructor
public class AuthUserRequest implements GameRequest, WithNicknameRequest {
    private String nickname;
}
