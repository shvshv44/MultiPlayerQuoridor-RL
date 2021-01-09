package com.rl.mpquoridor.models.gameroom;

import com.rl.mpquoridor.models.game.GameManager;
import com.rl.mpquoridor.models.players.WebSocketPlayer;
import lombok.Data;

import java.util.Map;

@Data
public class GameRoomState {

    private String id;
    private Map<String, WebSocketPlayer> players;
    private GameManager manager;
    private boolean isGameStarted;

}
