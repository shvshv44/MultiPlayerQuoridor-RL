package com.rl.mpquoridor.services;

import com.rl.mpquoridor.controllers.GameWebSocket;
import com.rl.mpquoridor.exceptions.InvalidOperationException;
import com.rl.mpquoridor.models.common.Constants;
import com.rl.mpquoridor.models.game.GameManager;
import com.rl.mpquoridor.models.gameroom.GameRoomState;
import com.rl.mpquoridor.models.players.Player;
import com.rl.mpquoridor.models.players.TCPPlayer;
import org.springframework.beans.factory.annotation.Autowired;
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
        state.setPlayers(new ArrayList<>());
        state.getPlayers().add(playerName);

        gameRooms.put(gameId, state);
        return gameId;
    }

    public void joinGame(String gameId, String playerName) {

        if (!gameRooms.containsKey(gameId))
            throw new InvalidOperationException("Game with id " + gameId + " does not exist!");

        GameRoomState roomState =  gameRooms.get(gameId);
        if (roomState.getPlayers().size() < Constants.MAX_NUMBER_PLAYERS) {
            roomState.getPlayers().add(playerName);
        } else  {
            throw new InvalidOperationException("Game room is full!");
        }
    }

    public void startGame(String gameId) {
        GameRoomState gameRoomState = gameRooms.get(gameId);
        GameManager gameManager = new GameManager(createPlayersFromNames(gameRoomState.getPlayers(), gameId), NUMBER_OF_WALLS_PER_PLAYER);
        gameRoomState.setManager(gameManager);
        gameManager.run();
    }

    public GameRoomState getRoomState(String gameId) {
        return gameRooms.get(gameId);
    }

    // TODO: Temporary function till TCP handler will be created!
    private List<Player> createPlayersFromNames(List<String> names, String gameId) {
        List<Player> players = new ArrayList<>();
        for (String name : names) {
            players.add(new TCPPlayer(name, gameId, webSocket));
        }

        return players;
    }

    public void assignWebSocket(GameWebSocket webSocket) {
        this.webSocket = webSocket;
    }

}
