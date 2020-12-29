package com.rl.mpquoridor.controllers;

import com.google.gson.Gson;
import com.rl.mpquoridor.models.actions.MovePawnAction;
import com.rl.mpquoridor.models.actions.PlaceWallAction;
import com.rl.mpquoridor.models.events.NewTurnEvent;
import com.rl.mpquoridor.models.gameroom.GameRoomState;
import com.rl.mpquoridor.models.gameroom.RoomStateRequest;
import com.rl.mpquoridor.models.gameroom.RoomStateResponse;
import com.rl.mpquoridor.services.GameRoomsManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GameWebSocket {

    private SimpMessagingTemplate messageSender;
    private GameRoomsManagerService roomsManager;
    private Gson gson;


    @Autowired
    public GameWebSocket(SimpMessagingTemplate messageSender, GameRoomsManagerService roomsManager, Gson gson) {
        this.messageSender = messageSender;
        this.roomsManager = roomsManager;
        this.gson = gson;
    }
    
    @MessageMapping("/hello")
    @SendTo("/topic/gameStatus")
    public boolean greeting(String message) throws Exception {
        return true;
    }

    @MessageMapping("/turnAction/{gameId}/movePawn")
    public void movePawn(@PathVariable String gameId, MovePawnAction action) {
        // todo: play the movePawn and return the game status
        this.messageSender.convertAndSend("/topic/gameStatus/" + gameId, action);
    }

    @MessageMapping("/turnAction/{gameId}/putWall")
    public void putWall(@PathVariable String gameId, PlaceWallAction action) {
        // todo: play the putWall and return the
        this.messageSender.convertAndSend("/topic/gameStatus/" + gameId, action);
    }

    @MessageMapping("/app/{gameId}/roomStateRequest")
    public void roomStateRequest(String requestAsString) {
        RoomStateRequest request = gson.fromJson(requestAsString, RoomStateRequest.class);
        System.out.println("Input: " + request);
        roomStateResponse(request);
    }

    public void roomStateResponse(RoomStateRequest request) {
        GameRoomState roomState = roomsManager.getRoomState(request.getGameID());
        RoomStateResponse response = new RoomStateResponse();
        response.setGameID(request.getGameID());
        response.setType("RoomStatusResponse");
        response.setPlayers(roomState.getPlayers());
        this.messageSender.convertAndSend("/topic/gameStatus/" + request.getGameID(), response);
    }

    public void endTurn(String gameId, NewTurnEvent action) {
        this.messageSender.convertAndSend("/topic/gameStatus/" + gameId, action);
    }
}
