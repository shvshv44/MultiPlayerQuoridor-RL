package com.rl.mpquoridor.models;

import com.rl.mpquoridor.enums.Direction;
import lombok.Data;

@Data
public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void moveByDirection(Direction direction) {
        if (direction == Direction.Up) {
            this.setY(this.y - 1);
        } else if (direction == Direction.Down) {
            this.setY(this.y + 1);
        } else if (direction == Direction.Right) {
            this.setX(this.x + 1);
        } else if (direction == Direction.Left) {
            this.setX(this.x - 1);
        }
    }
}
