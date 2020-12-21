package com.rl.mpquoridor.models.events;

import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;

public class TurnActionEvent implements GameEvent{
    private final TurnAction action;
    private final Pawn pawn;
    public TurnActionEvent(Pawn pawn, TurnAction action) {
        this.action = action;
        this.pawn = pawn;
    }

    public Pawn getPawn() {
        return pawn;
    }

    public TurnAction getAction() {
        return action;
    }
}
