package com.rl.mpquoridor.paths;

public class ShortestPathResult {

    private boolean pathFound;
    private ShortestPathNode shortestNode;

    public ShortestPathResult(boolean pathFound, ShortestPathNode shortestNode) {
        this.pathFound = pathFound;
        this.shortestNode = shortestNode;
    }

    public boolean isPathFound() {
        return pathFound;
    }

    public void setPathFound(boolean pathFound) {
        this.pathFound = pathFound;
    }

    public ShortestPathNode getShortestNode() {
        return shortestNode;
    }

    public void setShortestNode(ShortestPathNode shortestNode) {
        this.shortestNode = shortestNode;
    }
}
