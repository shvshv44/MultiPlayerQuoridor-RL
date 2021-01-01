package com.rl.mpquoridor.controllers;

import com.rl.mpquoridor.exceptions.InvalidOperationException;
import com.rl.mpquoridor.models.common.Constants;
import com.rl.mpquoridor.models.enums.WebSocketMessageType;
import com.rl.mpquoridor.models.game.GameResult;
import com.rl.mpquoridor.models.gameroom.GameRoomState;
import com.rl.mpquoridor.models.gameroom.StartGameEvent;
import com.rl.mpquoridor.services.GameRoomsManagerService;
import com.rl.mpquoridor.services.HistoryResolverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GameAPIController {

    private final static Logger logger = LoggerFactory.getLogger(GameAPIController.class);

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
        logger.info(playerName + " has been created game room with id: " + gameId);
        return gameId;
    }

    @CrossOrigin
    @GetMapping("/JoinGame/{gameId}/{playerName}")
    @ResponseBody
    public ResponseEntity<String> joinGame(@PathVariable String gameId, @PathVariable String playerName) {
        try {
            gameRoomManager.joinGame(gameId, playerName);
            logger.info("Player " + playerName + " has been joined to game room with id: " + gameId);
            return new ResponseEntity<>(gameId, new HttpHeaders(), HttpStatus.OK);
        } catch (InvalidOperationException ex) {
            return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @GetMapping("/StartGame/{gameId}")
    @ResponseBody
    public ResponseEntity<String> startGame(@PathVariable String gameId) {
        GameRoomState roomState = gameRoomManager.getRoomState(gameId);
        if (roomState.getPlayers().size() < Constants.MIN_NUMBER_PLAYERS)
            return new ResponseEntity<>("Game room must contain at least two players!", new HttpHeaders(), HttpStatus.BAD_REQUEST);

        startRoomGame(gameId);
        this.messageSender.convertAndSend("/topic/gameStatus/" + gameId, createStartGameEvent(gameId));
        return new ResponseEntity<>(gameId, new HttpHeaders(), HttpStatus.OK);
    }

    private void startRoomGame(@PathVariable String gameId) {
        try {
            logger.info("Starting game with id: " + gameId);
            gameRoomManager.startGame(gameId);
        } catch (Exception ex) {
            ex.printStackTrace(); // TODO: wont work till TCPPlayer will be implemented!
        }
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
        startGame.setType(WebSocketMessageType.START_GAME_EVENT);
        startGame.setGameID(gameId);
        startGame.setPlayers(roomState.getPlayers());
        startGame.setCurrentPlayerTurn(roomState.getPlayers().get(0));
        return startGame;
    }

}
