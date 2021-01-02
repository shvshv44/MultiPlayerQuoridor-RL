package com.rl.mpquoridor.models.websocket;

import com.rl.mpquoridor.models.common.WebSocketMessage;
import com.rl.mpquoridor.models.enums.WebSocketMessageType;
import com.rl.mpquoridor.models.gameroom.PlayerPosition;
import lombok.Data;

import java.util.Collection;

@Data
public class StartGameMessage extends WebSocketMessage {

    private Collection<PlayerPosition> players;

    public StartGameMessage() {
        this.setType(WebSocketMessageType.START_GAME_EVENT);
    }

}
