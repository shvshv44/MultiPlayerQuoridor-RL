package com.rl.mpquoridor.models.players;

import com.rl.mpquoridor.models.actions.MovePawnAction;
import com.rl.mpquoridor.models.actions.PlaceWallAction;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.ReadOnlyPhysicalBoard;
import com.rl.mpquoridor.models.common.EventMessage;
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

import java.util.*;

public abstract class SocketPlayer implements Player{

    protected String name;
    protected List<Pawn> playOrder;
    protected Pawn myPawn;
    protected ReadOnlyPhysicalBoard board;
    protected Map<Pawn, String> pawnPerPlayerName;

    public SocketPlayer(String name) {
        this.name = name;
    }

    public abstract void sendEvent(EventMessage message);

    @Override
    public final void trigger(GameEvent event) {
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
            this.sendEvent(endTurnEventMessage);
        }

        if (event instanceof StartGameEvent) {
            StartGameEvent startGameEvent = (StartGameEvent) event;
            this.pawnPerPlayerName = startGameEvent.getPawnPerPlayerName();

            StartGameMessage message = new StartGameMessage();
            message.setPlayers(new ArrayList<>());
            message.setNumOfWalls(((StartGameEvent) event).getNumOfWalls());
            for (Pawn currPawn : startGameEvent.getPawnPerPlayerName().keySet()) {
                PlayerPosition playerPosition = new PlayerPosition();
                playerPosition.setName(startGameEvent.getPawnPerPlayerName().get(currPawn));
                playerPosition.setPosition(this.board.getPawnPosition(currPawn));
                playerPosition.setEndLine(new HashSet<>(this.board.getPawnEndLine().get(currPawn)));
                message.getPlayers().add(playerPosition);
            }
            this.sendEvent(message);
        }

        if (event instanceof NewTurnEvent) {
            NewTurnEvent newTurnEvent = ((NewTurnEvent) event);
            NewTurnMessage message = new NewTurnMessage();
            message.setNextPlayerToPlay(pawnPerPlayerName.get(newTurnEvent.getPawn()));
            message.setCurrentPosition(board.getPawnPosition(myPawn));
            message.setSecondPlayerPosition(board.getPawnPosition(newTurnEvent.getSecondPawn()));
            message.setAvialiableMoves(this.myPawn == newTurnEvent.getPawn() ? newTurnEvent.getCurrentPawnMoves() : Collections.emptyList());
            message.setAvailableWalls(this.myPawn == newTurnEvent.getPawn() ? newTurnEvent.getCurrentPawnWalls() : Collections.emptyList());

            HashMap<String, Integer> paths = new HashMap<>();
            for (Map.Entry<Pawn, Integer> entry : newTurnEvent.getPawnShortestPath().entrySet()) {
                paths.put(this.pawnPerPlayerName.get(entry.getKey()), entry.getValue());
            }
            message.setPlayerShortestPaths(paths);

            this.sendEvent(message);
        }

        if (event instanceof GameOverEvent) {
            GameOverEvent gameOverEvent = (GameOverEvent) event;
            GameOverMessage message = new GameOverMessage();
            message.setWinnerName(this.pawnPerPlayerName.get(gameOverEvent.getWinner()));

            this.sendEvent(message);
        }
    }


    @Override
    public final void setPlayOrder(List<Pawn> playOrder) {
        this.playOrder = playOrder;
    }

    @Override
    public final void setMyPawn(Pawn myPawn) {
        this.myPawn = myPawn;
    }


    @Override
    public final String getPlayerName() {
        return this.name;
    }

    @Override
    public void setBoard(ReadOnlyPhysicalBoard board) {
        this.board = board;
    }


}
