package com.rl.mpquoridor.controllers;

import com.rl.mpquoridor.models.actions.MovePawn;
import com.rl.mpquoridor.models.actions.PlaceWall;
import com.rl.mpquoridor.models.actions.TurnAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GameWebSocket {

    @Autowired
    private SimpMessagingTemplate messageSender;


    @MessageMapping("/{gameId}/movePawn")
    public void movePawn(@PathVariable String gameId, MovePawn action) {
        // todo: play the movePawn and return the game status
        this.messageSender.convertAndSend("/topic/gameStatus/" + gameId, action);
    }

    @MessageMapping("/{gameId}/putWall")
    public void putWall(@PathVariable String gameId, PlaceWall action) {
        // todo: play the putWall and return the
        this.messageSender.convertAndSend("/topic/gameStatus/" + gameId, action);
    }


    public void endTurn(String gameId, TurnAction action) {
        this.messageSender.convertAndSend("/topic/gameStatus/" + gameId, action);
    }
}
