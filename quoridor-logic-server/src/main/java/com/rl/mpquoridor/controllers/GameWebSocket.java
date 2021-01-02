package com.rl.mpquoridor.controllers;

import com.google.gson.Gson;
import ch.qos.logback.core.joran.action.Action;
import com.rl.mpquoridor.models.actions.MovePawnAction;
import com.rl.mpquoridor.models.actions.PlaceWallAction;
import com.rl.mpquoridor.models.enums.WebSocketMessageType;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.events.EndTurnEvent;
import com.rl.mpquoridor.models.events.GameEvent;
import com.rl.mpquoridor.models.events.NewTurnEvent;
import com.rl.mpquoridor.models.gameroom.GameRoomState;
import com.rl.mpquoridor.models.gameroom.RoomStateRequest;
import com.rl.mpquoridor.models.gameroom.RoomStateResponse;
import com.rl.mpquoridor.models.players.TCPPlayer;
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

@Controller
public class GameWebSocket {

    private final static Logger logger = LoggerFactory.getLogger(GameWebSocket.class);

    private SimpMessagingTemplate messageSender;
    private GameRoomsManagerService roomsManager;
    private Gson gson;

    @Autowired
    public GameWebSocket(SimpMessagingTemplate messageSender, GameRoomsManagerService roomsManager, Gson gson) {
        this.messageSender = messageSender;
        this.roomsManager = roomsManager;
        this.gson = gson;

        this.roomsManager.assignWebSocket(this);
    }

    @MessageMapping("/hello")
    @SendTo("/topic/gameStatus")
    public boolean greeting(String message) throws Exception {
        return true;
    }

    @MessageMapping("/turnAction/{gameId}/{playerName}/movePawn")
    public void movePawn(@DestinationVariable String gameId, @DestinationVariable String playerName , MovePawnAction action) {
        notifyPlayer(gameId, playerName, action);
    }

    @MessageMapping("/turnAction/{gameId}/{playerName}/putWall")
    public void putWall(@PathVariable String gameId, @DestinationVariable String playerName , PlaceWallAction action) {
        notifyPlayer(gameId, playerName, action);
    }

    public void sendToPlayer(String gameId, String playerName, GameEvent event) {
        this.messageSender.convertAndSend("/topic/gameStatus/" + gameId + "/" + playerName, event);
    }

    @MessageMapping("/{gameId}/{playerName}/roomStateRequest")
    public void roomStateRequest(@DestinationVariable String gameId, @DestinationVariable String playerName, String requestAsString) {
        RoomStateRequest request = gson.fromJson(requestAsString, RoomStateRequest.class);
        logger.info("Game id " + gameId + " , Input : " + request);
        roomStateResponse(request, playerName);
    }

    private void roomStateResponse(RoomStateRequest request, String playerName) {
        GameRoomState roomState = roomsManager.getRoomState(request.getGameID());
        RoomStateResponse response = new RoomStateResponse();
        response.setGameID(request.getGameID());
        response.setType(WebSocketMessageType.ROOM_STATE_RESPONSE);
        response.setPlayers(roomState.getPlayers().keySet());

        for (TCPPlayer player: roomState.getPlayers().values()) {
            this.messageSender.convertAndSend("/topic/gameStatus/" + request.getGameID() + "/" + player.getPlayerName(), response);
        }
    }

    private void notifyPlayer(String gameId, String playerName, TurnAction action) {
        this.roomsManager.getRoomState(gameId).getPlayers().get(playerName).assignLastMove(action);
    }
}
