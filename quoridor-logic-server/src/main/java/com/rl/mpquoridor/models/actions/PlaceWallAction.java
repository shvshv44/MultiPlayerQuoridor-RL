package com.rl.mpquoridor.models.actions;

import com.rl.mpquoridor.models.board.Wall;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceWallAction implements TurnAction {
    private Wall wall;
}
