package com.rl.mpquoridor.models.websocket;

import com.rl.mpquoridor.models.common.WebSocketMessage;
import com.rl.mpquoridor.models.enums.WebSocketMessageType;
import lombok.Data;

@Data
public class RoomStateRequestMessage extends WebSocketMessage {

    public RoomStateRequestMessage() {
        this.setType(WebSocketMessageType.ROOM_STATE_REQUEST);
    }

}
