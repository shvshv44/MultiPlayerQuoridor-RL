package com.rl.mpquoridor.models.gameroom;

import com.rl.mpquoridor.models.game.GameManager;
import com.rl.mpquoridor.models.players.TCPPlayer;
import lombok.Data;

import java.util.Map;

@Data
public class GameRoomState {

    private String id;
    private Map<String, TCPPlayer> players;
    private GameManager manager;
    private boolean isGameStarted;

}
