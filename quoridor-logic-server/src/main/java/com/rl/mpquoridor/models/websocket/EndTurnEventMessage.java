package com.rl.mpquoridor.models.websocket;

import com.rl.mpquoridor.models.common.WebSocketMessage;
import com.rl.mpquoridor.models.enums.WebSocketMessageType;
import com.rl.mpquoridor.models.websocket.actions.WebSocketAction;
import lombok.Data;


@Data
public class EndTurnEventMessage extends WebSocketMessage {

    private WebSocketAction currentTurnMove;
    private String playerPlayed;

    public EndTurnEventMessage() {
        this.setType(WebSocketMessageType.END_TURN_EVENT);
    }

}
