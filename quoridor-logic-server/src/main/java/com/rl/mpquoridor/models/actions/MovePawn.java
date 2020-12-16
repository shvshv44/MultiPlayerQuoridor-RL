package com.rl.mpquoridor.models.actions;

import com.rl.mpquoridor.models.enums.MovementDirection;

public class MovePawn extends  TurnAction {
    MovementDirection direction;

    public MovePawn(MovementDirection direction) {
        this.direction = direction;
    }

    public MovementDirection getDirection() {
        return direction;
    }
}
