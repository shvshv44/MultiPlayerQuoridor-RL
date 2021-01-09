package com.rl.mpquoridor.models.players;

import com.rl.mpquoridor.controllers.GameWebSocket;
import com.rl.mpquoridor.models.actions.MovePawnAction;
import com.rl.mpquoridor.models.actions.PlaceWallAction;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.ReadOnlyPhysicalBoard;
import com.rl.mpquoridor.models.enums.WebSocketActionType;
import com.rl.mpquoridor.models.events.EndTurnEvent;
import com.rl.mpquoridor.models.events.GameEvent;
import com.rl.mpquoridor.models.events.NewTurnEvent;
import com.rl.mpquoridor.models.events.StartGameEvent;
import com.rl.mpquoridor.models.game.GameOverEvent;
import com.rl.mpquoridor.models.gameroom.PlayerPosition;
import com.rl.mpquoridor.models.websocket.EndTurnEventMessage;
import com.rl.mpquoridor.models.websocket.GameOverMessage;
import com.rl.mpquoridor.models.websocket.NewTurnMessage;
import com.rl.mpquoridor.models.websocket.StartGameMessage;
import com.rl.mpquoridor.models.websocket.actions.WebSocketAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.rl.mpquoridor.exceptions.IllegalMovementException.Reason;

public class WebSocketPlayer implements Player {
    private String name;
    private String gameId;
    private List<Pawn> playOrder;
    private Pawn myPawn;
    private ReadOnlyPhysicalBoard board;
    private GameWebSocket gameWebSocket;
    private TurnAction lastMove;
    private Map<Pawn, String> pawnPerPlayerName;

    public WebSocketPlayer(String name, String gameId, GameWebSocket gameWebSocket) {
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
            EndTurnEvent endTurnEvent = ((EndTurnEvent) event);
            TurnAction turnAction = endTurnEvent.getAction();
            WebSocketAction action = new WebSocketAction();

            if (turnAction instanceof MovePawnAction) {
                action.setType(WebSocketActionType.MOVE_PAWN);
                action.setPawnPosition(board.getPawnPosition(((EndTurnEvent) event).getPlayedPawn()));
            } else if (turnAction instanceof PlaceWallAction) {
                action.setType(WebSocketActionType.PLACE_WALL);
                action.setWall(((PlaceWallAction) turnAction).getWall());
                action.setNumOfWalls(this.board.getPawnWalls().get(endTurnEvent.getPlayedPawn()));
            }

            EndTurnEventMessage endTurnEventMessage = new EndTurnEventMessage();
            endTurnEventMessage.setCurrentTurnMove(action);
            endTurnEventMessage.setPlayerPlayed(this.pawnPerPlayerName.get(endTurnEvent.getPlayedPawn()));
            endTurnEventMessage.setGameID(gameId);
            gameWebSocket.sendToPlayer(gameId, name, endTurnEventMessage);
        }

        if (event instanceof StartGameEvent) {
            StartGameEvent startGameEvent = (StartGameEvent) event;
            this.pawnPerPlayerName = startGameEvent.getPawnPerPlayerName();

            StartGameMessage message = new StartGameMessage();
            message.setGameID(gameId);
            message.setPlayers(new ArrayList<>());
            message.setNumOfWalls(((StartGameEvent) event).getNumOfWalls());
            for (Pawn currPawn : startGameEvent.getPawnPerPlayerName().keySet()) {
                PlayerPosition playerPosition = new PlayerPosition();
                playerPosition.setName(startGameEvent.getPawnPerPlayerName().get(currPawn));
                playerPosition.setPosition(this.board.getPawnPosition(currPawn));

                message.getPlayers().add(playerPosition);
            }
            gameWebSocket.sendToPlayer(gameId, name, message);
        }

        if (event instanceof NewTurnEvent) {
            NewTurnEvent newTurnEvent = ((NewTurnEvent) event);
            NewTurnMessage message = new NewTurnMessage();
            message.setNextPlayerToPlay(pawnPerPlayerName.get(newTurnEvent.getPawn()));
            message.setGameID(gameId);
            message.setAvialiableMoves(this.myPawn == newTurnEvent.getPawn() ? newTurnEvent.getCurrentPawnMoves() : Collections.emptyList());

            gameWebSocket.sendToPlayer(gameId, name, message);
        }

        if (event instanceof GameOverEvent) {
            GameOverEvent gameOverEvent = (GameOverEvent) event;
            GameOverMessage message = new GameOverMessage();
            message.setGameID(gameId);
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
