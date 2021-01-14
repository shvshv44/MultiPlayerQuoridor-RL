package com.rl.mpquoridor.models.gameroom;

import com.rl.mpquoridor.models.game.GameManager;
import com.rl.mpquoridor.models.players.Player;
import com.rl.mpquoridor.models.players.SocketPlayer;
import lombok.Data;

import java.util.Map;

@Data
public class GameRoomState {

    private String id;
    private Map<String, SocketPlayer> players;
    private GameManager manager;
    private boolean isGameStarted;

}
