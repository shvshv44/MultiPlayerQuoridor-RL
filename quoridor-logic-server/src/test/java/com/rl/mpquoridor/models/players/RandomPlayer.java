package com.rl.mpquoridor.models.players;

import com.rl.mpquoridor.exceptions.IllegalMovementException;
import com.rl.mpquoridor.models.actions.MovePawnAction;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.ReadOnlyPhysicalBoard;
import com.rl.mpquoridor.models.enums.MovementDirection;
import com.rl.mpquoridor.models.events.GameEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class RandomPlayer implements Player {
    private static final Logger logger = LoggerFactory.getLogger(RandomPlayer.class);
    private static final Random rnd = new Random();

    @Override
    public void setPlayOrder(List<Pawn> playOrder) {

    }

    @Override
    public void setMyPawn(Pawn myPawn) {

    }

    @Override
    public void illegalMovePlayed(IllegalMovementException.Reason reason) {
    }

    @Override
    public void trigger(GameEvent event) {

    }

    @Override
    public void setBoard(ReadOnlyPhysicalBoard board) {

    }

    @Override
    public TurnAction play() {
        MovementDirection dir = MovementDirection.values()[rnd.nextInt(4)];
        logger.info("Random player tries to move " + dir.toString());
        return new MovePawnAction(dir);
    }
}
