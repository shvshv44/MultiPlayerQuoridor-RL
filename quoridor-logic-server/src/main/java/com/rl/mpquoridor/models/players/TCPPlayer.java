package com.rl.mpquoridor.models.players;

import com.rl.mpquoridor.controllers.GameWebSocket;
import com.rl.mpquoridor.models.actions.MovePawnAction;
import com.rl.mpquoridor.models.actions.ResponseMovePawnAction;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.Position;
import com.rl.mpquoridor.models.board.ReadOnlyPhysicalBoard;
import com.rl.mpquoridor.models.events.EndTurnEvent;
import com.rl.mpquoridor.models.events.GameEvent;
import com.rl.mpquoridor.models.events.StartGameEvent;
import com.rl.mpquoridor.models.events.TurnActionEvent;

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
        if (event instanceof TurnActionEvent) {
            TurnAction turnAction = ((TurnActionEvent) event).getAction();

            if (turnAction instanceof MovePawnAction) {
                turnAction = new ResponseMovePawnAction(board.getPawnPosition(((TurnActionEvent) event).getPawn()));
            }

            String nextPlayer = ((TurnActionEvent) event).getNextPlayer();
            boolean isGameEnded = ((TurnActionEvent) event).isGameEnded();
            List<Position> currentPlayerMoves = ((TurnActionEvent) event).getCurrentPlayerMoves();
            EndTurnEvent endTurnEvent = new EndTurnEvent(turnAction, nextPlayer,
                    isGameEnded, currentPlayerMoves);
            gameWebSocket.sendToPlayer(gameId, name, endTurnEvent);
        }

        if (event instanceof StartGameEvent) {
            //TODO: logic
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
