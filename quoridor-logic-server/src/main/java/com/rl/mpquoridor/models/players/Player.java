package com.rl.mpquoridor.models.players;

import com.rl.mpquoridor.models.Pawn;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.PhysicalBoard;
import com.rl.mpquoridor.models.board.ReadOnlyPhysicalBoard;

public abstract class Player {
    ReadOnlyPhysicalBoard board;

    public Player(ReadOnlyPhysicalBoard physicalBoard) {
        this.board = physicalBoard;
    }

    public abstract void setPlayOrder(Pawn[] playOrder);
    public abstract void setMyPawn(Pawn myPawn);
    public abstract void illegalMovePlayed(String msg);
    public abstract void triggerChange(); // todo: this signature could get Json or something.
    // public abstract void triggerChange(JsonObject json);
    public abstract TurnAction play();
    public abstract void setCurrentTurn(int pawnIdx);
}
