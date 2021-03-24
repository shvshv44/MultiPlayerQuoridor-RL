package com.rl.mpquoridor.models.actions;

public enum ActionType {
    MOVE_PAWN(MovePawnAction.class),
    PLACE_WALL(PlaceWallAction.class);

    ActionType(Class<? extends TurnAction> actionClass) {
        this.actionClass = actionClass;
    }

    private Class<? extends TurnAction> actionClass;

    public Class<? extends TurnAction> getActionClass() {
        return actionClass;
    }
}
