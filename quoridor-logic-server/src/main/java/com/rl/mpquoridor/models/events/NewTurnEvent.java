package com.rl.mpquoridor.models.events;

import com.rl.mpquoridor.models.board.Pawn;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NewTurnEvent implements GameEvent{
    Pawn pawn;
}
