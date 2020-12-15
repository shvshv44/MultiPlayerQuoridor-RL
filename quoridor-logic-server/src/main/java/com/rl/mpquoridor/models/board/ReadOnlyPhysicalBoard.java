package com.rl.mpquoridor.models.board;


import com.rl.mpquoridor.models.Pawn;
import com.rl.mpquoridor.models.Position;
import com.rl.mpquoridor.models.Wall;

import java.util.Map;
import java.util.Set;

public class ReadOnlyPhysicalBoard extends PhysicalBoard {
    PhysicalBoard source;
    public ReadOnlyPhysicalBoard(PhysicalBoard source) {
        this.source = source;
    }

    @Override
    public boolean movePawn(Pawn pawn, Position source, Position dest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean putWall(Pawn pawn, Wall wall) {
        throw new UnsupportedOperationException();
    }

    public Set<Wall> getWalls() {
        return source.getWalls();
    }

    public Map<Pawn, Position> getPawns() {
        return source.getPawns();
    }

    public Map<Pawn, Integer> getPawnWalls() {
        return source.getPawnWalls();
    }
}
