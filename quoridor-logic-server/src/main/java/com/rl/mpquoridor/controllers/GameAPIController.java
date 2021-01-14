package com.rl.mpquoridor.controllers;

import com.rl.mpquoridor.exceptions.InvalidOperationException;
import com.rl.mpquoridor.models.common.Constants;
import com.rl.mpquoridor.models.game.GameResult;
import com.rl.mpquoridor.models.gameroom.GameRoomState;
import com.rl.mpquoridor.models.players.WebSocketPlayer;
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

import java.net.ServerSocket;
import java.util.List;

@RestController
public class GameAPIController {

    private final static Logger logger = LoggerFactory.getLogger(GameAPIController.class);

    private GameRoomsManagerService gameRoomManager;
    private HistoryResolverService historyResolver;
    private SimpMessagingTemplate messageSender;
    private GameWebSocket gameWebSocket;
    private ServerSocket server;

    @Autowired
    public GameAPIController(GameRoomsManagerService gameRoomManager,
                             HistoryResolverService historyResolver,
                             SimpMessagingTemplate messageSender,
                             GameWebSocket gameWebSocket) {
        this.gameRoomManager = gameRoomManager;
        this.historyResolver = historyResolver;
        this.messageSender = messageSender;
        this.gameWebSocket = gameWebSocket;
    }

    @CrossOrigin
    @GetMapping("/CreateGame/{playerName}")
    @ResponseBody
    public ResponseEntity<String> createGame(@PathVariable String playerName) {
        if(playerName == null || playerName.equals(""))
            return createBasicBadRequestResponse("Name must contain at least one character!");

        String gameId = gameRoomManager.createGame();
        logger.info(playerName + " has been created game room with id: " + gameId);
        return this.joinGame(gameId, playerName);
    }

    @CrossOrigin
    @GetMapping("/JoinGame/{gameId}/{playerName}")
    @ResponseBody
    public ResponseEntity<String> joinGame(@PathVariable String gameId, @PathVariable String playerName) {

        if(playerName == null || playerName.equals(""))
            return createBasicBadRequestResponse("Name must contain at least one character!");

        if(gameRoomManager.getRoomState(gameId).getPlayers().containsKey(playerName))
            return createBasicBadRequestResponse("This name was already taken by another room member... replace it please.");

        if (gameRoomManager.getRoomState(gameId).isGameStarted())
            return createBasicBadRequestResponse("Cannot join room, the game is already started!");

        try {
            WebSocketPlayer player = new WebSocketPlayer(playerName, gameId, gameWebSocket);
            gameRoomManager.joinGame(gameId, player);
            logger.info("Player " + playerName + " has been joined to game room with id: " + gameId);
            return createBasicOKResponse(gameId);
        } catch (InvalidOperationException ex) {
            return createBasicBadRequestResponse(ex.getMessage());
        }
    }

    @CrossOrigin
    @GetMapping("/StartGame/{gameId}")
    @ResponseBody
    public ResponseEntity<String> startGame(@PathVariable String gameId) {
        GameRoomState roomState = gameRoomManager.getRoomState(gameId);
        if (roomState.getPlayers().size() < Constants.MIN_NUMBER_PLAYERS)
            return createBasicBadRequestResponse("Game room must contain at least two players!");

        if (roomState.isGameStarted())
            return createBasicBadRequestResponse("Game is already started!");

        startRoomGame(gameId);
        return createBasicOKResponse(gameId);
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

    private ResponseEntity<String> createBasicBadRequestResponse(String message) {
        return createBasicResponse(message, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<String> createBasicOKResponse(String message) {
        return createBasicResponse(message, HttpStatus.OK);
    }

    private ResponseEntity<String> createBasicResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(message, new HttpHeaders(), status);
    }


}
