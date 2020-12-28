package com.rl.mpquoridor.controllers;

import com.rl.mpquoridor.models.game.GameResult;
import com.rl.mpquoridor.services.GameRoomsManagerService;
import com.rl.mpquoridor.services.HistoryResolverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GameAPIController {

    private GameRoomsManagerService gameRoomManager;
    private HistoryResolverService historyResolver;

    @Autowired
    public GameAPIController(GameRoomsManagerService gameRoomManager, HistoryResolverService historyResolver) {
        this.gameRoomManager = gameRoomManager;
        this.historyResolver = historyResolver;
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
    public String joinGame(@PathVariable String gameId, @PathVariable String playerName) {
        gameRoomManager.joinGame(gameId, playerName);
        String output = "Player " + playerName + " has been joined to game room with id: " + gameId;
        System.out.println(output);
        return output;
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

        return "Game " + gameId + " has been started!";
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

}
