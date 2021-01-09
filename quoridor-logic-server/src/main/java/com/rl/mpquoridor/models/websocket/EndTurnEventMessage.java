package com.rl.mpquoridor.models.websocket;

import com.rl.mpquoridor.models.common.EventMessage;
import com.rl.mpquoridor.models.enums.MessageType;
import com.rl.mpquoridor.models.websocket.actions.WebSocketAction;
import lombok.Data;


@Data
public class EndTurnEventMessage extends EventMessage {

    private WebSocketAction currentTurnMove;
    private String playerPlayed;

    public EndTurnEventMessage() {
        this.setType(MessageType.END_TURN_EVENT);
    }

}
