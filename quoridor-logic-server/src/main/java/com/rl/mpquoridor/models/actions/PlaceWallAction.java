package com.rl.mpquoridor.models.actions;

import com.rl.mpquoridor.models.board.Wall;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaceWallAction implements TurnAction {
    private final Wall wall;
}
