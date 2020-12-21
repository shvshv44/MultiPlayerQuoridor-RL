package com.rl.mpquoridor.models.board;

import com.rl.mpquoridor.models.enums.WallDirection;

import java.util.Objects;

public class Wall {
    private final Position position;
    private final WallDirection wallDirection;

    public Wall(Position position, WallDirection wallDirection) {
        this.position = position;
        this.wallDirection = wallDirection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wall wall = (Wall) o;
        return Objects.equals(position, wall.position) &&
                wallDirection == wall.wallDirection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, wallDirection);
    }

    public WallDirection getWallDirection() {
        return wallDirection;
    }

    public Position getPosition() {
        return position;
    }

}
