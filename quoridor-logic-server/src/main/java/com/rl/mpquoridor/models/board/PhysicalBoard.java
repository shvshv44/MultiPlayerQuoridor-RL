package com.rl.mpquoridor.models.board;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.rl.mpquoridor.models.Pawn;
import com.rl.mpquoridor.models.Position;
import com.rl.mpquoridor.models.Wall;

import java.util.*;

public class PhysicalBoard {
    // todo Change the datasets and all other related methods in this class.
    HashSet<Wall> walls;
    BiMap<Pawn, Position> pawns;
    HashMap<Pawn, Integer> pawnWalls;

    public PhysicalBoard(Map<Pawn, Position> pawns, int numberOfWallsPerPlayer) {
        this.pawns = HashBiMap.create(pawns.size());
        this.pawnWalls = new HashMap<>();
        this.pawns.putAll(pawns);
        for (Pawn p : this.pawns.keySet()) {
            this.pawnWalls.put(p, numberOfWallsPerPlayer);
        }
    }

    protected PhysicalBoard() {
    }

    public Set<Wall> getWalls() {
        return Collections.unmodifiableSet(walls);
    }

    public Map<Pawn, Position> getPawns() {
        return Collections.unmodifiableMap(pawns);
    }

    public Map<Pawn, Integer> getPawnWalls() {
        return Collections.unmodifiableMap(pawnWalls);
    }

    public boolean movePawn(Pawn pawn,Position source, Position dest) {
        if(pawns.containsKey(pawn) && pawns.get(pawn).equals(source)) {
            pawns.put(pawn, dest);
            return true;
        }
        return false;
    }

    public boolean putWall(Pawn pawn, Wall wall) {
        if(pawnWalls.get(pawn) > 0) {
            pawnWalls.put(pawn, pawnWalls.get(pawn) - 1);
            this.walls.add(wall);
            return true;
        }
        return false;
    }
}
