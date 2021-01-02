package com.rl.mpquoridor.controllers;

import com.google.gson.Gson;
import ch.qos.logback.core.joran.action.Action;
import com.rl.mpquoridor.models.actions.MovePawnAction;
import com.rl.mpquoridor.models.actions.PlaceWallAction;
import com.rl.mpquoridor.models.enums.WebSocketMessageType;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.events.EndTurnEvent;
import com.rl.mpquoridor.models.events.NewTurnEvent;
import com.rl.mpquoridor.models.gameroom.GameRoomState;
import com.rl.mpquoridor.models.gameroom.RoomStateRequest;
import com.rl.mpquoridor.models.gameroom.RoomStateResponse;
import com.rl.mpquoridor.services.GameRoomsManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;

@Controller
public class GameWebSocket {

    private final static Logger logger = LoggerFactory.getLogger(GameWebSocket.class);

    private SimpMessagingTemplate messageSender;
    private GameRoomsManagerService roomsManager;
    private Gson gson;
    private Map<String,TurnAction> lastTurnAction;

    @Autowired
    public GameWebSocket(SimpMessagingTemplate messageSender, GameRoomsManagerService roomsManager, Gson gson) {
        this.messageSender = messageSender;
        this.roomsManager = roomsManager;
        this.gson = gson;
        lastTurnAction = new HashMap<>();

        this.roomsManager.assignWebSocket(this);
    }

    @MessageMapping("/hello")
    @SendTo("/topic/gameStatus")
    public boolean greeting(String message) throws Exception {
        return true;
    }

    @MessageMapping("/turnAction/{gameId}/movePawn")
    public void movePawn(@PathVariable String gameId, MovePawnAction action) {
        this.lastTurnAction.put(gameId,action);
    }

    @MessageMapping("/turnAction/{gameId}/putWall")
    public void putWall(@PathVariable String gameId, PlaceWallAction action) {
        this.lastTurnAction.put(gameId,action);
    }

    public void endTurn(String gameId, EndTurnEvent action) {
        this.messageSender.convertAndSend("/topic/gameStatus/" + gameId, action);
    }

    public TurnAction getLastTurnAction(String gameId) {
            return lastTurnAction.get(gameId);
    }

    @MessageMapping("/{gameId}/roomStateRequest")
    public void roomStateRequest(@DestinationVariable String gameId, String requestAsString) {
        RoomStateRequest request = gson.fromJson(requestAsString, RoomStateRequest.class);
        logger.info("Game id " + gameId + " , Input : " + request);
        roomStateResponse(request);
    }

    public void roomStateResponse(RoomStateRequest request) {
        GameRoomState roomState = roomsManager.getRoomState(request.getGameID());
        RoomStateResponse response = new RoomStateResponse();
        response.setGameID(request.getGameID());
        response.setType(WebSocketMessageType.ROOM_STATE_RESPONSE);
        response.setPlayers(roomState.getPlayers());
        this.messageSender.convertAndSend("/topic/gameStatus/" + request.getGameID(), response);
    }

    public void resetLastTurnAction(String gameId) {
        this.lastTurnAction.put(gameId, null);
    }
}
