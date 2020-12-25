package com.rl.mpquoridor.services;

import com.rl.mpquoridor.models.game.GameManager;
import com.rl.mpquoridor.models.game.GameRoomState;
import com.rl.mpquoridor.models.players.Player;
import com.rl.mpquoridor.models.players.TCPPlayer;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameRoomsManagerService {

    private final int NUMBER_OF_WALLS_PER_PLAYER = 8;

    private HashMap <String, GameRoomState> gameRooms;

    public GameRoomsManagerService() {
        this.gameRooms = gameRooms = new HashMap<>();
    }

    public String createGame(String playerName) {
        String gameId = UUID.randomUUID().toString();
        GameRoomState state = new GameRoomState();
        state.setId(gameId);
        state.setPlayers(new ArrayList<>());

        gameRooms.put(gameId, state);
        return gameId;
    }

    public void joinGame(String gameId, String playerName) {
        gameRooms.get(gameId).getPlayers().add(playerName);
    }

    public void startGame(String gameId) {
        GameRoomState gameRoomState = gameRooms.get(gameId);
        GameManager gameManager = new GameManager(createPlayersFromNames(gameRoomState.getPlayers()), NUMBER_OF_WALLS_PER_PLAYER);
        gameRoomState.setManager(gameManager);
        gameManager.run();
    }

    // TODO: Temporary function till TCP handler will be created!
    private List<Player> createPlayersFromNames(List<String> names) {
        List<Player> players = new ArrayList<>();
        for (String name : names) {
            players.add(new TCPPlayer(name));
        }

        return players;
    }

}
