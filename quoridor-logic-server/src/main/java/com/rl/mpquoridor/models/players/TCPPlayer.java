package com.rl.mpquoridor.models.players;

import com.rl.mpquoridor.controllers.GameWebSocket;
import com.rl.mpquoridor.models.actions.MovePawnAction;
import com.rl.mpquoridor.models.actions.PlaceWallAction;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.Position;
import com.rl.mpquoridor.models.board.ReadOnlyPhysicalBoard;
import com.rl.mpquoridor.models.enums.WebSocketActionType;
import com.rl.mpquoridor.models.enums.WebSocketMessageType;
import com.rl.mpquoridor.models.websocket.EndTurnEventMessage;
import com.rl.mpquoridor.models.events.GameEvent;
import com.rl.mpquoridor.models.events.StartGameEvent;
import com.rl.mpquoridor.models.events.EndTurnEvent;
import com.rl.mpquoridor.models.gameroom.PlayerPosition;
import com.rl.mpquoridor.models.websocket.StartGameMessage;
import com.rl.mpquoridor.models.websocket.actions.WebSocketAction;

import java.util.ArrayList;
import java.util.List;

import static com.rl.mpquoridor.exceptions.IllegalMovementException.Reason;

public class TCPPlayer implements Player {
    private String name;
    private String gameId;
    private List<Pawn> playOrder;
    private Pawn myPawn;
    private ReadOnlyPhysicalBoard board;
    private GameWebSocket gameWebSocket;
    private TurnAction lastMove;

    public TCPPlayer(String name, String gameId, GameWebSocket gameWebSocket) {
        this.name = name;
        this.gameId = gameId;
        this.gameWebSocket = gameWebSocket;
    }

    @Override
    public void setPlayOrder(List<Pawn> playOrder) {
        this.playOrder = playOrder;
    }

    @Override
    public void setMyPawn(Pawn myPawn) {
        this.myPawn = myPawn;
    }

    @Override
    public void illegalMovePlayed(Reason reason) {

    }

    @Override
    public void trigger(GameEvent event) {
        if (event instanceof EndTurnEvent) {
            TurnAction turnAction = ((EndTurnEvent) event).getAction();
            WebSocketAction action = new WebSocketAction();

            if (turnAction instanceof MovePawnAction) {
                action.setType(WebSocketActionType.MOVE_PAWN);
                action.setPawnPosition(board.getPawnPosition(((EndTurnEvent) event).getPlayedPawn()));
            } else if (turnAction instanceof PlaceWallAction) {
                action.setType(WebSocketActionType.PLACE_WALL);
                action.setWall(((PlaceWallAction) turnAction).getWall());
            }

            EndTurnEventMessage endTurnEvent = new EndTurnEventMessage(action);
            gameWebSocket.sendToPlayer(gameId, name, endTurnEvent);
        }

        if (event instanceof StartGameEvent) {
            //TODO: logic
            StartGameMessage message = new StartGameMessage();
            message.setGameID(gameId);
            message.setType(WebSocketMessageType.START_GAME_EVENT);
            message.setPlayers(new ArrayList<>());
            StartGameEvent startGameEvent = (StartGameEvent) event;
            for(Pawn currPawn: startGameEvent.getPawnPerPlayerName().keySet()) {
                PlayerPosition playerPosition = new PlayerPosition();
                playerPosition.setName(startGameEvent.getPawnPerPlayerName().get(currPawn));
                playerPosition.setPosition(new Position(1,1));

                message.getPlayers().add(playerPosition);
            }
            gameWebSocket.sendToPlayer(gameId, name, message);
        }
    }

    @Override
    public void setBoard(ReadOnlyPhysicalBoard board) {
        this.board = board;
    }

    @Override
    public String getPlayerName() {
        return this.name;
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
