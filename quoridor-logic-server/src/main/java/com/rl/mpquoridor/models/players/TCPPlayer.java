package com.rl.mpquoridor.models.players;

import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.ReadOnlyPhysicalBoard;
import com.rl.mpquoridor.models.events.GameEvent;

import java.util.List;

public class TCPPlayer implements Player {

    private String name;

    public TCPPlayer(String name) {
        this.name = name;
    }

    @Override
    public void setPlayOrder(List<Pawn> playOrder) {

    }

    @Override
    public void setMyPawn(Pawn myPawn) {

    }

    @Override
    public void illegalMovePlayed(String msg) {

    }

    @Override
    public void trigger(GameEvent event) {

    }

    @Override
    public void setBoard(ReadOnlyPhysicalBoard board) {

    }

    @Override
    public TurnAction play() {
        return null;
    }
}
