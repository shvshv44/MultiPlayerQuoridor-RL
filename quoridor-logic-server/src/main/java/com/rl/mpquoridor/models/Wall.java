package com.rl.mpquoridor.models;

import com.rl.mpquoridor.enums.Direction;
import lombok.Data;

@Data
public class Wall {
    private Position position;
    private Direction direction;

    public Wall(Position position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }
}
