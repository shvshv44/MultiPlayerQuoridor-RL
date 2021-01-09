package com.rl.mpquoridor.models.players;

import com.rl.mpquoridor.controllers.GameWebSocket;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.common.EventMessage;

import static com.rl.mpquoridor.exceptions.IllegalMovementException.Reason;

public class WebSocketPlayer extends SocketPlayer {
    private String gameId;
    private GameWebSocket gameWebSocket;
    private TurnAction lastMove;

    public WebSocketPlayer(String name, String gameId, GameWebSocket gameWebSocket) {
        super(name);
        this.gameId = gameId;
        this.gameWebSocket = gameWebSocket;
    }

    @Override
    public void illegalMovePlayed(Reason reason) {

    }

    @Override
    public void sendEvent(EventMessage message) {
        gameWebSocket.sendToPlayer(gameId, name, message);
    }


    @Override
    public TurnAction play() {
        while (this.lastMove == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        TurnAction turnAction = this.lastMove;
        resetLastMove();

        return turnAction;
    }

    public void assignLastMove(TurnAction turnAction) {
        this.lastMove = turnAction;
    }

    private void resetLastMove() {
        this.lastMove = null;
    }
}
