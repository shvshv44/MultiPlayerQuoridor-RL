package com.rl.mpquoridor.models.gameroom;

import com.rl.mpquoridor.models.common.WebSocketMessage;
import lombok.Data;

@Data
public class RoomStateRequest extends WebSocketMessage {
    private String gameID;
}
