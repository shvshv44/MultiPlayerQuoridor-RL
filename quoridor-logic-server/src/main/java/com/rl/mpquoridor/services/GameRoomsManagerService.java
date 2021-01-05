package com.rl.mpquoridor.services;

import com.rl.mpquoridor.controllers.GameWebSocket;
import com.rl.mpquoridor.exceptions.InvalidOperationException;
import com.rl.mpquoridor.models.common.Constants;
import com.rl.mpquoridor.models.game.GameManager;
import com.rl.mpquoridor.models.gameroom.GameRoomState;
import com.rl.mpquoridor.models.players.TCPPlayer;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.rl.mpquoridor.models.common.Constants.NUMBER_OF_WALLS_PER_PLAYER;

@Service
public class GameRoomsManagerService {

    private HashMap <String, GameRoomState> gameRooms;
    private GameWebSocket webSocket;

    public GameRoomsManagerService() {
        this.gameRooms = new HashMap<>();
    }

    public String createGame(String playerName) {
        String gameId = UUID.randomUUID().toString();
        GameRoomState state = new GameRoomState();
        state.setId(gameId);
        state.setPlayers(new HashMap<>());
        state.getPlayers().put(playerName, new TCPPlayer(playerName, gameId, webSocket));
        state.setGameStarted(false);

        gameRooms.put(gameId, state);
        return gameId;
    }

    public void joinGame(String gameId, String playerName) {

        if (!gameRooms.containsKey(gameId))
            throw new InvalidOperationException("Game with id " + gameId + " does not exist!");

        GameRoomState roomState =  gameRooms.get(gameId);
        if (roomState.getPlayers().size() < Constants.MAX_NUMBER_PLAYERS) {
            roomState.getPlayers().put(playerName, new TCPPlayer(playerName, gameId, webSocket));
        } else  {
            throw new InvalidOperationException("Game room is full!");
        }
    }

    public void startGame(String gameId) {
        GameRoomState gameRoomState = gameRooms.get(gameId);
        GameManager gameManager = new GameManager(new ArrayList<>(gameRoomState.getPlayers().values()), NUMBER_OF_WALLS_PER_PLAYER);
        gameRoomState.setManager(gameManager);
        gameRoomState.setGameStarted(true);

        new Thread(() -> gameManager.run()).start();
    }

    public GameRoomState getRoomState(String gameId) {
        return gameRooms.get(gameId);
    }

    public void assignWebSocket(GameWebSocket webSocket) {
        this.webSocket = webSocket;
    }

}
