package com.rl.mpquoridor.paths;

import com.rl.mpquoridor.models.board.Position;

import java.util.Objects;

public class ShortestPathNode {

    private Position pos;
    private ShortestPathNode prevNode;

    public ShortestPathNode(Position pos, ShortestPathNode prevNode) {
        this.pos = pos;
        this.prevNode = prevNode;
    }

    public int getLengthFromStart() {
        int length = 0;
        ShortestPathNode currentNode = this;

        if (currentNode.prevNode == null)
            return length;

        while (currentNode.prevNode != null) {
            length++;
            currentNode = currentNode.prevNode;
        }

        return length;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public ShortestPathNode getPrevNode() {
        return prevNode;
    }

    public void setPrevNode(ShortestPathNode prevNode) {
        this.prevNode = prevNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShortestPathNode that = (ShortestPathNode) o;
        return pos.equals(that.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos);
    }
}
