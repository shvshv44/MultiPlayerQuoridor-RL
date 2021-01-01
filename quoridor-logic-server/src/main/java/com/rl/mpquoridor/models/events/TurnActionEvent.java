package com.rl.mpquoridor.models.events;

import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class TurnActionEvent implements GameEvent {
    private final Pawn pawn;
    private final TurnAction action;
    private final String nextPlayer;
    private final boolean isGameEnded;
    private List<Position> currentPlayerMoves;
}
