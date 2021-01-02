package com.rl.mpquoridor.models.game;

import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.events.GameEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GameOverEvent implements GameEvent {

    private Pawn winner;

}
