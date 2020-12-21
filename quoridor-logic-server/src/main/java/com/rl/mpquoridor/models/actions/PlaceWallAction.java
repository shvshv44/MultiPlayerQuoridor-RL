package com.rl.mpquoridor.models.actions;

import com.rl.mpquoridor.models.board.Wall;

public class PlaceWallAction implements TurnAction {
    private final Wall wall;

    public PlaceWallAction(Wall wall) {
        this.wall = wall;
    }

    public Wall getWall() {
        return wall;
    }
}
