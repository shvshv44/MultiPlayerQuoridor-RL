package com.rl.mpquoridor.models.game;

import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;

public class HistoryRecord {
    Pawn pawn;
    TurnAction action;

    public HistoryRecord(Pawn pawn, TurnAction action) {
        this.pawn = pawn;
        this.action = action;
    }

    public Pawn getPawn() {
        return this.pawn;
    }

    public TurnAction getAction() {
        return action;
    }
}
