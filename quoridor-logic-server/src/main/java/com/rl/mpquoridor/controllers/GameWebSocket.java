package com.rl.mpquoridor.controllers;

import com.rl.mpquoridor.models.Position;
import com.rl.mpquoridor.models.actions.TurnAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class GameWebSocket {

    @Autowired
    private SimpMessagingTemplate messageSender;


    @MessageMapping("/movePawn/{gameId}")
    public void movePawn(@PathVariable String gameId, TurnAction action) throws Exception {
        // todo: play the movePawn and return the game status
        this.messageSender.convertAndSend("/topic/gameStatus/" + gameId, action);
    }

    @MessageMapping("/putWall/{gameId}")
    public void putWall(@PathVariable String gameId, TurnAction action) throws Exception {
        // todo: play the putWall and return the
        this.messageSender.convertAndSend("/topic/gameStatus/" + gameId, action);
    }


    public void endTurn(String gameId, TurnAction action) throws Exception {
        this.messageSender.convertAndSend("/topic/gameStatus/" + gameId, action);
    }


    public void avaliableMoves(String gameId, List<Position> positions) throws Exception {
        // send the available moves of the next turn
        this.messageSender.convertAndSend("/topic/availableMoves/" + gameId, positions);
    }
}
