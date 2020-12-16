package com.rl.mpquoridor.models;

import com.rl.mpquoridor.models.enums.WallDirection;

import java.util.Objects;

public class Wall {
    private Position pos;
    private WallDirection wallDirection;

    public Wall(Position pos, WallDirection wallDirection) {
        this.pos = pos;
        this.wallDirection = wallDirection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wall wall = (Wall) o;
        return Objects.equals(pos, wall.pos) &&
                wallDirection == wall.wallDirection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, wallDirection);
    }

    public WallDirection getWallDirection() {
        return wallDirection;
    }

    public void setWallDirection(WallDirection wallDirection) {
        this.wallDirection = wallDirection;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }
}
