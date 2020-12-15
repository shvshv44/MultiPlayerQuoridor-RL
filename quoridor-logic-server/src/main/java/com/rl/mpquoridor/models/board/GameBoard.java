package com.rl.mpquoridor.models.board;

import com.rl.mpquoridor.exceptions.IllegalMovementException;
import com.rl.mpquoridor.models.Pawn;
import com.rl.mpquoridor.models.Wall;
import com.rl.mpquoridor.models.actions.MovePawn;
import com.rl.mpquoridor.models.actions.PlaceWall;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.enums.MovementDirection;

// todo: implement methods in this class.
public class GameBoard {
    private PhysicalBoard board;
    private ReadOnlyPhysicalBoard readOnlyPhysicalBoard;

    public GameBoard(int numberOfPlayers, int numberOfWallsPerPlayer) {
        System.out.println("not yet implemented");
    }

    private void movePawn(Pawn pawn, MovementDirection direction) throws IllegalMovementException {
        System.out.println("not yet implemented");
    }

    private void placeWall (Pawn pawn, Wall wall)  throws IllegalMovementException {
        System.out.println("not yet implemented");
    }

    public void executeAction(Pawn pawn, TurnAction action) throws  IllegalMovementException{
        if(action instanceof PlaceWall) {
            placeWall(pawn, ((PlaceWall) action).getWall());
        } else if(action instanceof MovePawn) {
            movePawn(pawn, ((MovePawn) action).getDirection());
        }
    }

    /**
     *
     * @return a readonly copy of the physical board
     */
    public PhysicalBoard getPhysicalBoard() {
        if(this.readOnlyPhysicalBoard == null) {
            this.readOnlyPhysicalBoard = new ReadOnlyPhysicalBoard(this.board);
        }
        return  this.readOnlyPhysicalBoard;
    }

    /**
     * Should be an O(1) implementation.
     *  @return the winner pawn or null
     */
    public Pawn winner() {
        System.out.println("not yet implemented");
        return null;
    }
}
