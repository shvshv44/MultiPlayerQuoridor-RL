package com.rl.mpquoridor.models.players;

import com.rl.mpquoridor.controllers.GameWebSocket;
import com.rl.mpquoridor.exceptions.IllegalMovementException;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.ReadOnlyPhysicalBoard;
import com.rl.mpquoridor.models.events.GameEvent;

import java.util.List;
import java.util.Map;

public class TCPPlayer implements Player {

    private String name;
    private String gameId;
    private List<Pawn> playOrder;
    private Pawn myPawn;
    private ReadOnlyPhysicalBoard board;
    private GameTCPSocket gameWebSocket;
    private TurnAction lastMove;
    private Map<Pawn, String> pawnPerPlayerName;

    public TCPPlayer(String name, String gameId, GameTCPSocket gameTCPSocket) {
        this.name = name;
        this.gameId = gameId;
        this.gameWebSocket = gameTCPSocket;
    }

    @Override
    public void setPlayOrder(List<Pawn> playOrder) {

    }

    @Override
    public void setMyPawn(Pawn myPawn) {

    }

    @Override
    public void illegalMovePlayed(IllegalMovementException.Reason reason) {

    }

    @Override
    public void trigger(GameEvent event) {

    }

    @Override
    public void setBoard(ReadOnlyPhysicalBoard board) {

    }

    @Override
    public String getPlayerName() {
        return null;
    }

    @Override
    public TurnAction play() {
        return null;
    }
}
