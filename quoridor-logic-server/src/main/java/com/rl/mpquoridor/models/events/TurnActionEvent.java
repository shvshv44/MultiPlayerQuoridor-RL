package com.rl.mpquoridor.models.events;

import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TurnActionEvent implements GameEvent{
    private final Pawn pawn;
    private final TurnAction action;
}
