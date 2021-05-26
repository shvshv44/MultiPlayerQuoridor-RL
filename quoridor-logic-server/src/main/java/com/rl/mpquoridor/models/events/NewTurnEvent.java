package com.rl.mpquoridor.models.events;

import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.Position;
import com.rl.mpquoridor.models.board.Wall;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@Getter
public class NewTurnEvent implements GameEvent{
    Pawn pawn;
    Pawn secondPawn;
    List<Position> currentPawnMoves;
    Set<Wall> currentPawnWalls;
    Map<Pawn, Integer> pawnShortestPath;
}
