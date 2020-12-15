package com.rl.mpquoridor.models.actions;

import com.rl.mpquoridor.models.Wall;

public class PlaceWall  extends TurnAction {
    private final Wall wall;

    public PlaceWall(Wall wall) {
        this.wall = wall;
    }

    public Wall getWall() {
        return wall;
    }
}
