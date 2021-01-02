package com.rl.mpquoridor.models.websocket;

import com.rl.mpquoridor.models.common.WebSocketMessage;
import com.rl.mpquoridor.models.enums.WebSocketMessageType;
import lombok.Data;

@Data
public class GameOverMessage extends WebSocketMessage {

    public GameOverMessage() {
        this.setType(WebSocketMessageType.GAME_OVER_EVENT);
    }

    private String winnerName;

}
