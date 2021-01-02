package com.rl.mpquoridor.models.websocket;

import com.rl.mpquoridor.models.common.WebSocketMessage;
import com.rl.mpquoridor.models.gameroom.PlayerPosition;
import lombok.Data;

import java.util.Collection;

@Data
public class RoomStateResponseMessage extends WebSocketMessage {
    private Collection<PlayerPosition> players;
}
