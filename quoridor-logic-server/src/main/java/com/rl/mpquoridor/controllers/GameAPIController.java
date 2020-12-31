package com.rl.mpquoridor.controllers;

import com.rl.mpquoridor.exceptions.InvalidOperationException;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.events.TurnActionEvent;
import com.rl.mpquoridor.models.game.GameResult;
import com.rl.mpquoridor.models.gameroom.GameRoomState;
import com.rl.mpquoridor.models.gameroom.StartGameEvent;
import com.rl.mpquoridor.services.GameRoomsManagerService;
import com.rl.mpquoridor.services.HistoryResolverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GameAPIController {

    private GameRoomsManagerService gameRoomManager;
    private HistoryResolverService historyResolver;
    private SimpMessagingTemplate messageSender;

    @Autowired
    public GameAPIController(GameRoomsManagerService gameRoomManager,
                             HistoryResolverService historyResolver,
                             SimpMessagingTemplate messageSender) {
        this.gameRoomManager = gameRoomManager;
        this.historyResolver = historyResolver;
        this.messageSender = messageSender;
    }

    @CrossOrigin
    @GetMapping("/CreateGame/{playerName}")
    @ResponseBody
    public String createGame(@PathVariable String playerName) {
        String gameId = gameRoomManager.createGame(playerName);
        System.out.println(playerName + " has been created game room with id: " + gameId);
        return gameId;
    }

    @CrossOrigin
    @GetMapping("/JoinGame/{gameId}/{playerName}")
    @ResponseBody
    public ResponseEntity<String> joinGame(@PathVariable String gameId, @PathVariable String playerName) {
        try {
            gameRoomManager.joinGame(gameId, playerName);
            System.out.println("Player " + playerName + " has been joined to game room with id: " + gameId);
            return new ResponseEntity<>(gameId, new HttpHeaders(), HttpStatus.OK);
        } catch (InvalidOperationException ex) {
            return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @GetMapping("/StartGame/{gameId}")
    @ResponseBody
    public String startGame(@PathVariable String gameId) {
        try {
            System.out.println("Starting game with id: " + gameId);
            gameRoomManager.startGame(gameId);
        } catch (Exception ex) {
            ex.printStackTrace(); // TODO: wont work till TCPPlayer will be implemented!
        }

        this.messageSender.convertAndSend("/topic/gameStatus/" + gameId, createStartGameEvent(gameId));
        return gameId;
    }

    @CrossOrigin
    @GetMapping("/History")
    @ResponseBody
    public List<String> history() {
        return historyResolver.fetchAllHistoryGameIds();
    }

    @CrossOrigin
    @GetMapping("/History/{gameId}")
    @ResponseBody
    public GameResult historyByGameId(@PathVariable String gameId) {
        return historyResolver.getResultByGameId(gameId);
    }

    private StartGameEvent createStartGameEvent(String gameId) {
        GameRoomState roomState = gameRoomManager.getRoomState(gameId);
        StartGameEvent startGame = new StartGameEvent();
        startGame.setType("StartGameEvent");
        startGame.setGameID(gameId);
        startGame.setPlayers(roomState.getPlayers());
        startGame.setCurrentPlayerTurn(roomState.getPlayers().get(0));
        return startGame;
    }

}
