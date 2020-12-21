package com.rl.mpquoridor.models.events;

import com.rl.mpquoridor.models.board.Pawn;

public class NewTurnEvent implements GameEvent{
    Pawn pawn;

    public NewTurnEvent(Pawn pawn) {
        this.pawn = pawn;
    }

    public Pawn getPawn() {
        return pawn;
    }
}
