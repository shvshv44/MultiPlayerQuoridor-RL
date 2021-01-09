package com.rl.mpquoridor.models.websocket;

import com.rl.mpquoridor.models.common.EventMessage;
import com.rl.mpquoridor.models.enums.MessageType;
import lombok.Data;

@Data
public class GameOverMessage extends EventMessage {
    private String winnerName;

    public GameOverMessage() {
        this.setType(MessageType.GAME_OVER_EVENT);
    }
}
