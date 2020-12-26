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
        return gameRoomManager.createGame(playerName);
    }

    @CrossOrigin
    @GetMapping("/JoinGame/{gameId}/{playerName}")
    @ResponseBody
    public String joinGame(@PathVariable String gameId, @PathVariable String playerName) {
        gameRoomManager.joinGame(gameId, playerName);
        return "Player " + playerName + " has been joined to game " + gameId;
    }

    @CrossOrigin
    @GetMapping("/StartGame/{gameId}")
    @ResponseBody
    public String startGame(@PathVariable String gameId) {
        try {
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
