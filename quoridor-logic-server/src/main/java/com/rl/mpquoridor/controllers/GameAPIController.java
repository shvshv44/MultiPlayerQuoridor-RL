package com.rl.mpquoridor.controllers;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GameAPIController {


    @GetMapping("/CreateGame")
    @ResponseBody
    public String createGame() {
        return "Game has been started!";
    }

    @GetMapping("/JoinGame/{gameId}")
    @ResponseBody
    public String joinGame(@PathVariable String gameId) {
        return "Join to game id " + gameId;
    }

    @GetMapping("/History")
    @ResponseBody
    public List<String> history() {
        return new ArrayList<>();
    }

    @GetMapping("/History/{gameId}")
    @ResponseBody
    public String historyByGameId(@PathVariable String gameId) {
        return "Get all history of: " + gameId;
    }

}
