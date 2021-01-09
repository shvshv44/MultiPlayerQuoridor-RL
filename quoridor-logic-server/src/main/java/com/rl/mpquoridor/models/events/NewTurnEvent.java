package com.rl.mpquoridor.models.events;

import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.Position;
import com.rl.mpquoridor.models.board.Wall;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
public class NewTurnEvent implements GameEvent{
    Pawn pawn;
    List<Position> currentPawnMoves;
    Set<Wall> currentPawnWalls;

}
