package com.rl.mpquoridor.controllers;

import ch.qos.logback.core.joran.action.Action;
import com.rl.mpquoridor.models.actions.MovePawnAction;
import com.rl.mpquoridor.models.actions.PlaceWallAction;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.events.EndTurnEvent;
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
    private TurnAction lastTurnAction;

    @MessageMapping("/turnAction/{gameId}/movePawn")
    public void movePawn(@PathVariable String gameId, MovePawnAction action) {
        lastTurnAction = action;
    }

    @MessageMapping("/turnAction/{gameId}/putWall")
    public void putWall(@PathVariable String gameId, PlaceWallAction action) {
        lastTurnAction = action;
    }

    public void endTurn(String gameId, EndTurnEvent action) {
        this.messageSender.convertAndSend("/topic/gameStatus/" + gameId, action);
    }

    public TurnAction getLastTurnAction() {
            return lastTurnAction;
    }

    public void setLastTurnAction(TurnAction lastTurnAction) {
        this.lastTurnAction = lastTurnAction;
    }
}
