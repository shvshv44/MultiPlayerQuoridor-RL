package com.rl.mpquoridor.models.game;

import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HistoryRecord {
    private final Pawn pawn;
    private final TurnAction action;
}
