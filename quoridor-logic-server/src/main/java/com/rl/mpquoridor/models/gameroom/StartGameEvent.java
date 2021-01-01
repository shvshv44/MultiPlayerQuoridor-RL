package com.rl.mpquoridor.models.gameroom;

import com.rl.mpquoridor.models.common.WebSocketMessage;
import lombok.Data;

import java.util.List;

@Data
public class StartGameEvent extends WebSocketMessage {

    private List<String> players;
    private String currentPlayerTurn;

}
