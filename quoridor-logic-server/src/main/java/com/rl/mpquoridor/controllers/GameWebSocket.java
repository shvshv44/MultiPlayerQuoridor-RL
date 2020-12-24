package com.rl.mpquoridor.controllers;

import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.events.NewTurnEvent;
import com.rl.mpquoridor.models.events.TurnActionEvent;
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
    public void movePawn(@PathVariable String gameId, TurnActionEvent action) {
        // todo: play the movePawn and return the game status
        this.messageSender.convertAndSend("/topic/gameStatus/" + gameId, action);
    }

    @MessageMapping("/{gameId}/putWall")
    public void putWall(@PathVariable String gameId, TurnActionEvent action) {
        // todo: play the putWall and return the
        this.messageSender.convertAndSend("/topic/gameStatus/" + gameId, action);
    }


    public void endTurn(String gameId, NewTurnEvent action) {
        this.messageSender.convertAndSend("/topic/gameStatus/" + gameId, action);
    }
}
