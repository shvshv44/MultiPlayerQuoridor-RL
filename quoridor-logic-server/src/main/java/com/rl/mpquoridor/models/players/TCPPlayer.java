package com.rl.mpquoridor.models.players;

import com.rl.mpquoridor.controllers.GameWebSocket;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.Position;
import com.rl.mpquoridor.models.board.ReadOnlyPhysicalBoard;
import com.rl.mpquoridor.models.events.EndTurnEvent;
import com.rl.mpquoridor.models.events.GameEvent;
import com.rl.mpquoridor.models.events.TurnActionEvent;

import java.util.List;

public class TCPPlayer implements Player {

    private String name;
    private String gameId;
    private List<Pawn> playOrder;
    private Pawn myPawn;
    private ReadOnlyPhysicalBoard board;
    private GameWebSocket gameWebSocket = new GameWebSocket();

    public TCPPlayer(String name, String gameId) {
        this.name = name;
        this.gameId = gameId;
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
    public void illegalMovePlayed(String msg) {

    }

    @Override
    public void trigger(GameEvent event) {
        if (event instanceof TurnActionEvent) {
            TurnAction turnAction = ((TurnActionEvent) event).getAction();
            String nextPlayer = ((TurnActionEvent) event).getNextPlayer();
            boolean isGameEnded = ((TurnActionEvent) event).isGameEnded();
            List<Position> currentPlayerMoves = ((TurnActionEvent) event).getCurrentPlayerMoves();
            EndTurnEvent endTurnEvent = new EndTurnEvent(turnAction, nextPlayer,
                    isGameEnded, currentPlayerMoves);
            gameWebSocket.endTurn(gameId, endTurnEvent);

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
        while (gameWebSocket.getLastTurnAction() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        TurnAction turnAction = gameWebSocket.getLastTurnAction();
        gameWebSocket.setLastTurnAction(null);

        return turnAction;
    }
}
