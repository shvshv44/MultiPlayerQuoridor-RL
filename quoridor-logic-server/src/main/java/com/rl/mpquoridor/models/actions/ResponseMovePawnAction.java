package com.rl.mpquoridor.models.actions;

import com.rl.mpquoridor.models.board.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResponseMovePawnAction implements TurnAction {
    Position newPawnPosition;
}
