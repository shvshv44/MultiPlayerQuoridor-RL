package com.rl.mpquoridor.models.board;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * ------------------------------------------
 * Every procedure you create in this class
 * you have to make sure that you create
 * the same in ReadOnlyPhysicalBoard class
 * -------------------------------------------
 */
public class PhysicalBoard {
    private Set<Wall> walls;
    private BiMap<Pawn, Position> pawns;
    private Map<Pawn, Integer> pawnWalls;
    private int startNumberOfWallsPerPlayer;
    private Map<Pawn, Set<Position>> pawnEndLine;


    public PhysicalBoard(int numberOfWallsPerPlayer) {
        this.pawnWalls = new HashMap<>();
        this.startNumberOfWallsPerPlayer = numberOfWallsPerPlayer;
        this.walls = Sets.newConcurrentHashSet();

    }

    public void setPawnPosition (Map<Pawn, Position> pawns) {
        this.pawns = HashBiMap.create(pawns.size());
        this.pawns.putAll(pawns);
        for (Pawn p : this.pawns.keySet()) {
            this.pawnWalls.put(p, this.startNumberOfWallsPerPlayer);
        }
    }

    protected PhysicalBoard() {
    }

    public Set<Wall> getWalls() {
        return Collections.unmodifiableSet(walls);
    }

    public Set<Pawn> getPawns() {
        return Collections.unmodifiableSet(pawns.keySet());
    }

    public Map<Pawn, Integer> getPawnWalls() {
        return Collections.unmodifiableMap(pawnWalls);
    }

    public void movePawn(Pawn pawn, Position dest) {
        if (this.pawns.containsKey(pawn)) {
            pawns.put(pawn, dest);
        }
    }

    public void putWall(Wall wall) {
        this.walls.add(wall);
    }
    public void removeWall(Wall wall) {
        this.walls.remove(wall);
    }

    public void reduceWallToPawn(Pawn p) {
        if (pawns.containsKey(p)) {
            this.pawnWalls.put(p, this.pawnWalls.get(p) - 1);
        }
    }

    public int getSize() {
        return 9;
    }

    public Pawn pawnAt(Position p) {
        return pawns.inverse().get(p);
    }

    public Position getPawnPosition(Pawn pawn) {
        return this.pawns.get(pawn);
    }

    public Map<Pawn, Position> getAllPawnPosition() {
        return Collections.unmodifiableMap(this.pawns);
    }

    public Map<Pawn, Set<Position>> getPawnEndLine() {
        return pawnEndLine;
    }

    public void setPawnEndLine(final Map<Pawn, Set<Position>> pawnEndLine) {
        this.pawnEndLine = pawnEndLine;
    }

}
