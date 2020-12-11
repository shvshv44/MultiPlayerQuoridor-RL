package com.rl.mpquoridor.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameAPIController {


    @GetMapping("/startGame")
    @ResponseBody
    public String startGame() {
        return "Game has been started!";
    }

}
