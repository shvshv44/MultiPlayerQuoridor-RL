package com.rl.mpquoridor.models.players;

import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.ReadOnlyPhysicalBoard;
import com.rl.mpquoridor.models.events.GameEvent;

import java.util.List;

import static com.rl.mpquoridor.exceptions.IllegalMovementException.Reason;

public interface Player {
    void setPlayOrder(List<Pawn> playOrder);
    void setMyPawn(Pawn myPawn);
    void illegalMovePlayed(Reason reason);
    void trigger(GameEvent event);
    void setBoard(ReadOnlyPhysicalBoard board);
    String getPlayerName();
    TurnAction play();
}
