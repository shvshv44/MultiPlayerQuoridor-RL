package com.rl.mpquoridor.models.actions;

import com.rl.mpquoridor.models.enums.MovementDirection;

public class MovePawnAction implements   TurnAction {
    MovementDirection direction;

    public MovePawnAction(MovementDirection direction) {
        this.direction = direction;
    }

    public MovementDirection getDirection() {
        return direction;
    }
}
