package com.rl.mpquoridor.models.websocket;

import com.rl.mpquoridor.models.common.EventMessage;
import com.rl.mpquoridor.models.enums.MessageType;
import com.rl.mpquoridor.models.gameroom.PlayerPosition;
import lombok.Data;

import java.util.Collection;

@Data
public class StartGameMessage extends EventMessage {

    private Collection<PlayerPosition> players;
    private int numOfWalls;

    public StartGameMessage() {
        this.setType(MessageType.START_GAME_EVENT);
    }
}
