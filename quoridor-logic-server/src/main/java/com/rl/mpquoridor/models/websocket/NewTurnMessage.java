package com.rl.mpquoridor.models.websocket;

import com.rl.mpquoridor.models.board.Position;
import com.rl.mpquoridor.models.board.Wall;
import com.rl.mpquoridor.models.common.EventMessage;
import com.rl.mpquoridor.models.enums.MessageType;
import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
public class NewTurnMessage extends EventMessage {

    public NewTurnMessage() {
        this.setType(MessageType.NEW_TURN_EVENT);
    }

    private String nextPlayerToPlay;
    private List<Position> avialiableMoves;
    private Collection<Wall> availableWalls;

}
