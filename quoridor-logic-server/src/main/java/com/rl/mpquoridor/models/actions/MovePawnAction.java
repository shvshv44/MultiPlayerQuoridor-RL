package com.rl.mpquoridor.models.actions;

import com.rl.mpquoridor.models.enums.MovementDirection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovePawnAction implements TurnAction {
    private MovementDirection direction;

    @Override
    public ActionType getActionType() {
        return ActionType.MOVE_PAWN;
    }
}
