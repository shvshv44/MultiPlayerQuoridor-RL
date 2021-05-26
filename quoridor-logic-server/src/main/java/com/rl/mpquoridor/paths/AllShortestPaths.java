package com.rl.mpquoridor.paths;

import com.rl.mpquoridor.models.board.Pawn;

import java.util.HashMap;
import java.util.Map;

public class AllShortestPaths {

    private Map<Pawn, Integer> pawnShortestPath;
    private boolean isAllHavePaths;

    public AllShortestPaths(boolean isAll) {
        this.pawnShortestPath = new HashMap<>();
        this.isAllHavePaths = isAll;
    }

    public Map<Pawn, Integer> getPawnShortestPath() {
        return pawnShortestPath;
    }

    public void add(Pawn p, Integer pathSize) {
        pawnShortestPath.put(p, pathSize);
    }

    public boolean isAllHavePaths() {
        return isAllHavePaths;
    }
}
