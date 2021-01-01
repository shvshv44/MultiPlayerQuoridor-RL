package com.rl.mpquoridor.models.gameroom;

import com.rl.mpquoridor.models.common.WebSocketMessage;
import lombok.Data;

import java.util.List;

@Data
public class RoomStateResponse extends WebSocketMessage {
    private List<String> players;
}