package com.rl.mpquoridor.models.events;

import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class NewTurnEvent implements GameEvent{
    Pawn pawn;
    List<Position> currentPawnMoves;

}
