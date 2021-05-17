package com.rl.mpquoridor.models.board;


import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class ReadOnlyPhysicalBoard extends PhysicalBoard {
    private final PhysicalBoard source;

    public ReadOnlyPhysicalBoard(PhysicalBoard source) {
        this.source = source;
    }

    @Override
    public void movePawn(Pawn pawn, Position dest) {
        throw new UnsupportedOperationException();
    }

    public Set<Wall> getWalls() {
        return source.getWalls();
    }

    public Set<Pawn> getPawns() {
        return source.getPawns();
    }

    public Map<Pawn, Integer> getPawnWalls() {
        return source.getPawnWalls();
    }

    @Override
    public int getSize() {
        return source.getSize();
    }

    @Override
    public void putWall(Wall wall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeWall(Wall wall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reduceWallToPawn(Pawn p) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pawn pawnAt(Position p) {
        return source.pawnAt(p);
    }

    @Override
    public Position getPawnPosition(Pawn pawn) {
        return source.getPawnPosition(pawn);
    }

    @Override
    public Map<Pawn, Position> getAllPawnPosition() {
        return source.getAllPawnPosition();
    }

    @Override
    public Map<Pawn, Set<Position>> getPawnEndLine() {
        return Collections.unmodifiableMap(source.getPawnEndLine());
    }

    @Override
    public void setPawnWalls(Map<Pawn, Integer> pawnWalls) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWalls(Set<Wall> walls) {
        throw new UnsupportedOperationException();
    }
}

