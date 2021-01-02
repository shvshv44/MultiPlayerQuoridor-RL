package com.rl.mpquoridor.models.websocket;

import com.rl.mpquoridor.models.board.Position;
import com.rl.mpquoridor.models.common.WebSocketMessage;
import com.rl.mpquoridor.models.enums.WebSocketMessageType;
import lombok.Data;

import java.util.List;

@Data
public class NewTurnMessage extends WebSocketMessage {

    public NewTurnMessage() {
        this.setType(WebSocketMessageType.NEW_TURN_EVENT);
    }

    private String nextPlayerToPlay;
    private List<Position> avialiableMoves;

}
