package com.rl.mpquoridor.models.websocket;

import com.rl.mpquoridor.models.common.WebSocketMessage;
import com.rl.mpquoridor.models.websocket.actions.WebSocketAction;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class EndTurnEventMessage extends WebSocketMessage {
    private WebSocketAction currentTurnMove;
}
