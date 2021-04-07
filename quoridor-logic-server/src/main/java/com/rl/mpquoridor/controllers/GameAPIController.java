package com.rl.mpquoridor.controllers;

import com.rl.mpquoridor.exceptions.InvalidOperationException;
import com.rl.mpquoridor.models.board.Position;
import com.rl.mpquoridor.models.board.ReadOnlyPhysicalBoard;
import com.rl.mpquoridor.models.board.Wall;
import com.rl.mpquoridor.models.common.Constants;
import com.rl.mpquoridor.models.enums.WallDirection;
import com.rl.mpquoridor.models.game.GameManager;
import com.rl.mpquoridor.models.gameroom.GameRoomState;
import com.rl.mpquoridor.models.players.Player;
import com.rl.mpquoridor.models.players.WebSocketPlayer;
import com.rl.mpquoridor.services.GameRoomsManagerService;
import com.rl.mpquoridor.services.HistoryResolverService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@RestController
public class GameAPIController {

    private final static Logger logger = LoggerFactory.getLogger(GameAPIController.class);

    private GameRoomsManagerService gameRoomManager;
    private HistoryResolverService historyResolver;
    private SimpMessagingTemplate messageSender;
    private GameWebSocket gameWebSocket;
    private RestTemplate restTemplate;
    private ServerSocket server;
    private String pythonServerURL = System.getenv("PYTHON_SERVER_URL"); // "http://localhost:8000/"
    private String addAgentToGameEndpoint = "addAgentToGame/";
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Document NOT_FOUND_DOCUMENT = new Document("not_found", 1);

    @Autowired
    public GameAPIController(GameRoomsManagerService gameRoomManager,
                             HistoryResolverService historyResolver,
                             SimpMessagingTemplate messageSender,
                             GameWebSocket gameWebSocket,
                             RestTemplate restTemplate) {
        this.gameRoomManager = gameRoomManager;
        this.historyResolver = historyResolver;
        this.messageSender = messageSender;
        this.gameWebSocket = gameWebSocket;
        this.restTemplate = restTemplate;
    }

    @CrossOrigin
    @GetMapping("/BoardStatus/{gameId}")
    @ResponseBody
    public Map<String, Object> boardStatus(@PathVariable String gameId) {
        GameRoomState gameRoomState = gameRoomManager.getRoomState(gameId);
        GameManager gameManager = gameRoomState.getManager();

        Queue<Player> copiedPlayers = new LinkedList<>(gameManager.getPlayers());

        List<Position> playersPositions = copiedPlayers.stream().map(gameManager.getPlayerPawn()::get)
                .map(gameManager.getGameBoard().getReadOnlyPhysicalBoard()::getPawnPosition).collect(Collectors.toList());

        int[][] horizontalWalls = new int[9][9];
        int[][] verticalWalls = new int[9][9];

        horizontalWalls = initHorizontalWalls(gameRoomState.getManager().getGameBoard().getReadOnlyPhysicalBoard());
        verticalWalls = initVerticalWalls(gameRoomState.getManager().getGameBoard().getReadOnlyPhysicalBoard());

        Map<String, Object> map = new HashMap<>();
        map.put("players", playersPositions);
        map.put("horizontalWalls", horizontalWalls);
        map.put("verticalWalls", verticalWalls);

        return map;
    }

    private int[][] initVerticalWalls(ReadOnlyPhysicalBoard board) {
        int[][] verticalWalls = new int[9][9];

        board.getWalls().stream()
                .filter(wall -> WallDirection.DOWN.equals(wall.getWallDirection()))
                .map(Wall::getPosition)
                .forEach(pos -> verticalWalls[pos.getY()][pos.getX()] = 1);


        return verticalWalls;
    }

    private int[][] initHorizontalWalls(ReadOnlyPhysicalBoard board) {
        int[][] horizontalWalls = new int[9][9];

        board.getWalls().stream()
                .filter(wall -> WallDirection.RIGHT.equals(wall.getWallDirection()))
                .map(Wall::getPosition)
                .forEach(pos -> horizontalWalls[pos.getY()][pos.getX()] = 1);


        return horizontalWalls;
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
    @GetMapping("/CreateGameAgainstAgent/{playerName}")
    @ResponseBody
    public ResponseEntity<String> createGameAgainstAgent(@PathVariable String playerName) {
        String gameId = gameRoomManager.createGame();
        logger.info(playerName + " has been created game room with id: " + gameId);

        ResponseEntity<String> joinGameResponse= this.joinGame(gameId, playerName);
        executor.submit(() -> joinAgentToTheGame(gameId));

        return joinGameResponse;
    }

    private void joinAgentToTheGame(String gameId) {
        ResponseEntity<String> response = restTemplate.getForEntity(pythonServerURL + addAgentToGameEndpoint + gameId, String.class);
        logger.info("Send joining agent to the python server with game id " + gameId);
        logger.info("Python server response " + response.getBody());
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
    public List<Document> history() {
        return this.historyResolver.fetchHistory();
    }

    @CrossOrigin
    @GetMapping("/HistoryIds")
    @ResponseBody
    public List<Document> historyGameIds() {
        return this.historyResolver.fetchHistoryGameIds();
    }

    @CrossOrigin
    @GetMapping("/History/{id}")
    @ResponseBody
    public ResponseEntity<Document> historyByGameId(@PathVariable String id) {
        Document d = historyResolver.getById(id);
        if( d == null) {
            return createBasicResponse(NOT_FOUND_DOCUMENT, HttpStatus.NOT_FOUND);
        } else {
            return createBasicResponse(d, HttpStatus.OK);

        }
    }

    private ResponseEntity<String> createBasicBadRequestResponse(String message) {
        return createBasicResponse(message, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<String> createBasicOKResponse(String message) {
        return createBasicResponse(message, HttpStatus.OK);
    }

    private <T> ResponseEntity<T> createBasicResponse(T message, HttpStatus status) {
        return new ResponseEntity<>(message, new HttpHeaders(), status);
    }


}
