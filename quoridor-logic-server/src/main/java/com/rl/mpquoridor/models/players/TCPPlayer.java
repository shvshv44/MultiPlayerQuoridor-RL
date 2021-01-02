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
import com.rl.mpquoridor.models.events.NewTurnEvent;
import com.rl.mpquoridor.models.game.GameOverEvent;
import com.rl.mpquoridor.models.websocket.EndTurnEventMessage;
import com.rl.mpquoridor.models.events.GameEvent;
import com.rl.mpquoridor.models.events.StartGameEvent;
import com.rl.mpquoridor.models.events.EndTurnEvent;
import com.rl.mpquoridor.models.gameroom.PlayerPosition;
import com.rl.mpquoridor.models.websocket.GameOverMessage;
import com.rl.mpquoridor.models.websocket.NewTurnMessage;
import com.rl.mpquoridor.models.websocket.StartGameMessage;
import com.rl.mpquoridor.models.websocket.actions.WebSocketAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.rl.mpquoridor.exceptions.IllegalMovementException.Reason;

public class TCPPlayer implements Player {
    private String name;
    private String gameId;
    private List<Pawn> playOrder;
    private Pawn myPawn;
    private ReadOnlyPhysicalBoard board;
    private GameWebSocket gameWebSocket;
    private TurnAction lastMove;
    private Map<Pawn, String> pawnPerPlayerName;

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
            StartGameEvent startGameEvent = (StartGameEvent) event;
            this.pawnPerPlayerName = startGameEvent.getPawnPerPlayerName();

            StartGameMessage message = new StartGameMessage();
            message.setGameID(gameId);
            message.setPlayers(new ArrayList<>());
            for(Pawn currPawn: startGameEvent.getPawnPerPlayerName().keySet()) {
                PlayerPosition playerPosition = new PlayerPosition();
                playerPosition.setName(startGameEvent.getPawnPerPlayerName().get(currPawn));
                playerPosition.setPosition(this.board.getPawnPosition(currPawn));

                message.getPlayers().add(playerPosition);
            }
            gameWebSocket.sendToPlayer(gameId, name, message);
        }

        if (event instanceof NewTurnEvent) {
            //TODO: logic
            NewTurnEvent newTurnEvent = ((NewTurnEvent) event);
            NewTurnMessage message = new NewTurnMessage();
            message.setNextPlayerToPlay(pawnPerPlayerName.get(newTurnEvent.getPawn()));
            message.setGameID(gameId);
            message.setAvialiableMoves(newTurnEvent.getCurrentPawnMoves());

            gameWebSocket.sendToPlayer(gameId, name, message);
        }

        if(event instanceof GameOverEvent) {
            GameOverEvent gameOverEvent = (GameOverEvent) event;
            GameOverMessage message = new GameOverMessage();
            message.setWinnerName(this.pawnPerPlayerName.get(gameOverEvent.getWinner()));

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
