package com.rl.mpquoridor.models.players;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rl.mpquoridor.controllers.GameTCPSocket;
import com.rl.mpquoridor.exceptions.IllegalMovementException;
import com.rl.mpquoridor.models.actions.MovePawnAction;
import com.rl.mpquoridor.models.actions.PlaceWallAction;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.common.EventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TCPPlayer extends SocketPlayer {

    private static final String ASK_FOR_PLAY_MESSAGE = "play";
    private static final Logger logger = LoggerFactory.getLogger(TCPPlayer.class);
    private static final Gson gson = new Gson();

    private GameTCPSocket gameTCPSocket;

    public TCPPlayer(String name, GameTCPSocket gameTCPSocket) {
        super(name);
        this.gameTCPSocket = gameTCPSocket;
    }

    @Override
    protected void sendEvent(EventMessage message) {
        try {
            gameTCPSocket.send(gson.toJson(message, message.getClass()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void illegalMovePlayed(IllegalMovementException.Reason reason) {
        // TODO
    }


    @Override
    public TurnAction play() {
        while(true) {
            try {
                gameTCPSocket.send(ASK_FOR_PLAY_MESSAGE);
                String msg = gameTCPSocket.read();
                return parseMessageToTurnAction(msg);
            } catch (JsonParseException e) {
                logger.error("TCP Player " + this.name + " got JsonParseException while playing", e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private TurnAction parseMessageToTurnAction(String msg) {
        JsonObject json = gson.fromJson(msg, JsonObject.class);
        if(json.keySet().contains("wall")) {
            return gson.fromJson(msg, PlaceWallAction.class);
        } else {
            return gson.fromJson(msg, MovePawnAction.class);
        }
    }
}
