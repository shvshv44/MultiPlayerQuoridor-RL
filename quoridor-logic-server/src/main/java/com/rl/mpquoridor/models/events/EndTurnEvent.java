package com.rl.mpquoridor.models.events;

import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class EndTurnEvent implements GameEvent {
    private TurnAction currentTurnMove;
    private String nextPlayerTurn;
    private boolean isGameEnded;
    private List<Position> currentPlayerMoves;
}
