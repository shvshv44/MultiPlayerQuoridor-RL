package com.rl.mpquoridor.models.events;

import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class EndTurnEvent implements GameEvent {
    private final Pawn playedPawn;
    private final TurnAction action;
}
