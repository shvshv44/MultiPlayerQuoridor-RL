package com.rl.mpquoridor.models.websocket;

import com.rl.mpquoridor.models.common.EventMessage;
import com.rl.mpquoridor.models.enums.MessageType;
import lombok.Data;

import java.util.Collection;

@Data
public class RoomStateResponseMessage extends EventMessage {
    private Collection<String> players;

    public RoomStateResponseMessage() {
        this.setType(MessageType.ROOM_STATE_RESPONSE);
    }
}
