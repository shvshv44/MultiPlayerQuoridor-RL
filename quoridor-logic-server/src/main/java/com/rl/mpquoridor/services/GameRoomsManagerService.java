package com.rl.mpquoridor.services;

import com.rl.mpquoridor.exceptions.InvalidOperationException;
import com.rl.mpquoridor.models.common.Constants;
import com.rl.mpquoridor.models.game.GameManager;
import com.rl.mpquoridor.models.gameroom.GameRoomState;
import com.rl.mpquoridor.models.players.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.rl.mpquoridor.models.common.Constants.NUMBER_OF_WALLS_PER_PLAYER;

@Service
public class GameRoomsManagerService {

    private final HashMap <String, GameRoomState> gameRooms;

    public GameRoomsManagerService() {
        this.gameRooms = new HashMap<>();
    }

    public String createGame() {
        String gameId = UUID.randomUUID().toString();
        GameRoomState state = new GameRoomState();
        state.setId(gameId);
        state.setPlayers(new HashMap<>());
        state.setGameStarted(false);

        gameRooms.put(gameId, state);
        return gameId;
    }

    public void joinGame(String gameId, Player player) {

        if (!gameRooms.containsKey(gameId))
            throw new InvalidOperationException("Game with id " + gameId + " does not exist!");

        GameRoomState roomState =  gameRooms.get(gameId);
        if (roomState.getPlayers().size() < Constants.MAX_NUMBER_PLAYERS) {
            roomState.getPlayers().put(player.getPlayerName(), player);
        } else  {
            throw new InvalidOperationException("Game room is full!");
        }
    }

    public void startGame(String gameId) {
        GameRoomState gameRoomState = gameRooms.get(gameId);
        GameManager gameManager = new GameManager(new ArrayList<>(gameRoomState.getPlayers().values()), NUMBER_OF_WALLS_PER_PLAYER);
        gameRoomState.setManager(gameManager);
        gameRoomState.setGameStarted(true);

        new Thread(gameManager::run).start();
    }

    public GameRoomState getRoomState(String gameId) {
        return gameRooms.get(gameId);
    }

}
