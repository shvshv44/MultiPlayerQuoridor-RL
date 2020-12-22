package com.rl.mpquoridor.models.actions;

import com.rl.mpquoridor.models.enums.MovementDirection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MovePawnAction implements TurnAction {
    private final MovementDirection direction;
}
