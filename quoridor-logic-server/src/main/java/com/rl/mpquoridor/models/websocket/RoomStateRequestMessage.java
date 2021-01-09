package com.rl.mpquoridor.models.websocket;

import com.rl.mpquoridor.models.common.EventMessage;
import com.rl.mpquoridor.models.enums.MessageType;
import lombok.Data;

@Data
public class RoomStateRequestMessage extends EventMessage {

    private String gameID;
    public RoomStateRequestMessage() {
        this.setType(MessageType.ROOM_STATE_REQUEST);
    }



}
