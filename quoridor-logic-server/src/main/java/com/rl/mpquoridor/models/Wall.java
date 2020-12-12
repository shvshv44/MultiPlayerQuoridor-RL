package com.rl.mpquoridor.models;

import com.rl.mpquoridor.enums.Direction;
import lombok.Data;

@Data
public class Wall {
    private Position position;
    private Direction direction;
}
