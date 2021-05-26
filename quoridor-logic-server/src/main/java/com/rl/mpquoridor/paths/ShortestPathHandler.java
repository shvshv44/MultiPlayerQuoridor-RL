package com.rl.mpquoridor.paths;

import com.rl.mpquoridor.models.board.PhysicalBoard;
import com.rl.mpquoridor.models.board.Position;
import com.rl.mpquoridor.models.board.Wall;
import com.rl.mpquoridor.models.enums.WallDirection;

import java.util.*;

public class ShortestPathHandler {

    public ShortestPathResult getShortestPath(Position s, Set<Position> dest, PhysicalBoard physicalBoard) {
        Queue<ShortestPathNode> q = new LinkedList<>();
        Set<ShortestPathNode> visited = new HashSet<>();
        q.add(new ShortestPathNode(s, null));
        while (!q.isEmpty()) {
            ShortestPathNode currNode = q.poll();
            Position currPos = currNode.getPos();
            if (dest.contains(currPos)) { // This is an O(1) operation
                return new  ShortestPathResult(true, currNode);
            }
            if (!visited.contains(currNode)) {
                visited.add(currNode);
                q.addAll(getAvailableNeighbors(currNode, physicalBoard));
            }
        }

        return new  ShortestPathResult(false, null);
    }

    private List<ShortestPathNode> getAvailableNeighbors(ShortestPathNode node, PhysicalBoard physicalBoard) {
        List<ShortestPathNode> ret = new LinkedList<>();
        Position p = node.getPos();
        // UP
        if (p.getY() > 0 && Collections.disjoint(physicalBoard.getWalls(), getUpBlockers(p))) {
            ret.add(new ShortestPathNode(new Position(p.getY() - 1, p.getX()), node));
        }
        // DOWN
        if (p.getY() < physicalBoard.getSize() - 1 && Collections.disjoint(physicalBoard.getWalls(), getDownBlockers(p))) {
            ret.add(new ShortestPathNode(new Position(p.getY() + 1, p.getX()), node));
        }

        // LEFT
        if (p.getX() > 0 && Collections.disjoint(physicalBoard.getWalls(), getLeftBlockers(p))) {
            ret.add(new ShortestPathNode(new Position(p.getY(), p.getX() - 1), node));
        }

        // RIGHT
        if (p.getX() < physicalBoard.getSize() - 1 && Collections.disjoint(physicalBoard.getWalls(), getRightBlockers(p))) {
            ret.add(new ShortestPathNode(new Position(p.getY(), p.getX() + 1), node));
        }

        return ret;
    }


    private List<Wall> getUpBlockers(Position p) {
        List<Wall> ret = new LinkedList<>();
        ret.add(new Wall(new Position(p.getY() - 1, p.getX() - 1), WallDirection.RIGHT));
        ret.add(new Wall(new Position(p.getY() - 1, p.getX()), WallDirection.RIGHT));
        return ret;
    }

    private List<Wall> getDownBlockers(Position p) {
        List<Wall> ret = new LinkedList<>();
        ret.add(new Wall(new Position(p.getY(), p.getX()), WallDirection.RIGHT));
        ret.add(new Wall(new Position(p.getY(), p.getX() - 1), WallDirection.RIGHT));
        return ret;
    }

    private List<Wall> getLeftBlockers(Position p) {
        List<Wall> ret = new LinkedList<>();
        ret.add(new Wall(new Position(p.getY() - 1, p.getX() - 1), WallDirection.DOWN));
        ret.add(new Wall(new Position(p.getY(), p.getX() - 1), WallDirection.DOWN));
        return ret;
    }

    private List<Wall> getRightBlockers(Position p) {
        List<Wall> ret = new LinkedList<>();
        ret.add(new Wall(new Position(p.getY() - 1, p.getX()), WallDirection.DOWN));
        ret.add(new Wall(new Position(p.getY(), p.getX()), WallDirection.DOWN));
        return ret;
    }

}
