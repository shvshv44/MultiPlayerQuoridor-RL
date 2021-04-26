package com.rl.mpquoridor.tests;

import com.rl.mpquoridor.exceptions.IllegalMovementException;
import com.rl.mpquoridor.models.actions.MovePawnAction;
import com.rl.mpquoridor.models.actions.PlaceWallAction;
import com.rl.mpquoridor.models.board.GameBoard;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.Position;
import com.rl.mpquoridor.models.board.Wall;
import com.rl.mpquoridor.models.enums.MovementDirection;
import com.rl.mpquoridor.models.enums.WallDirection;
import org.springframework.data.util.Pair;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.rl.mpquoridor.exceptions.IllegalMovementException.Reason.*;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

@Test
public class GameRulesTest {

    private static final String ILLEGAL_OPERATION_ALLOWED = "Test failed: the operation should not be allowed";
    private GameBoard board;
    private Pawn p;

    @BeforeMethod
    @SuppressWarnings("all")
    private void resetBoard() {
        this.board = new GameBoard(2, 8);
        p = this.board.getReadOnlyPhysicalBoard().getPawns().stream().filter(p -> this.board.getReadOnlyPhysicalBoard().getPawnPosition(p).equals(new Position(0,4))).findFirst().get();
    }

    @Test
    private void WallPlacementNormal() {
        Wall w = new Wall(new Position(2,3), WallDirection.DOWN);
        this.board.executeAction(p, new PlaceWallAction(w));
        assertTrue(this.board.getReadOnlyPhysicalBoard().getWalls().contains(w));
    }

    @Test
    private void WallPlacementAtEdge() {
        Wall w = new Wall(new Position(7,2), WallDirection.RIGHT);
        this.board.executeAction(p, new PlaceWallAction(w));
        assertTrue(this.board.getReadOnlyPhysicalBoard().getWalls().contains(w));
    }

    @Test
    private void PlaceWallWhenNoneLeft() {
        for (int i = 0; i < 8; i++) {
            Wall w = new Wall(new Position(i, 0), WallDirection.RIGHT);
            this.board.executeAction(p, new PlaceWallAction(w));
        }
        try {
            this.board.executeAction(p, new PlaceWallAction(new Wall(new Position(3,3), WallDirection.RIGHT)));
            throw new RuntimeException(ILLEGAL_OPERATION_ALLOWED);
        } catch (IllegalMovementException e) {
            assertEquals(e.getReason(), NO_WALLS_LEFT);
        }
    }
    @Test
    private void WallPlacementOutsideBounds() {
        Wall w = new Wall(new Position(9,0), WallDirection.DOWN);
        try {
            this.board.executeAction(p, new PlaceWallAction(w));
            throw new RuntimeException(ILLEGAL_OPERATION_ALLOWED);
        } catch (IllegalMovementException e) {
            assertEquals(e.getReason(), WALL_IS_OUTSIDE_THE_BOARD_BOUNDS);
        }

    }

    @Test
    private void WallPlacementCollision() {
        Wall w1 = new Wall(new Position(1, 2), WallDirection.DOWN);
        this.board.executeAction(p, new PlaceWallAction(w1));
        Wall[] blockers = new Wall[] {
                new Wall(new Position(0,2), WallDirection.DOWN),
                new Wall(new Position(2,2), WallDirection.DOWN),
                new Wall(new Position(1,2), WallDirection.RIGHT)
        };
        for (Wall w : blockers) {
            try {
                this.board.executeAction(p, new PlaceWallAction(w));
                throw new RuntimeException(ILLEGAL_OPERATION_ALLOWED);
            } catch (IllegalMovementException e) {
                assertEquals(e.getReason(), WALL_COLLIDES_WITH_OTHER_WALL);
            }
        }

    }

    @Test
    private void WallPlacementBlockPath() {
        Wall[] blockers = new Wall[] {
                new Wall(new Position(0,3), WallDirection.DOWN),
                new Wall(new Position(1,4), WallDirection.RIGHT),
                new Wall(new Position(0,5), WallDirection.DOWN)
        };
        this.board.executeAction(p, new PlaceWallAction(blockers[0]));
        this.board.executeAction(p, new PlaceWallAction(blockers[1]));

        try {
            this.board.executeAction(p, new PlaceWallAction(blockers[2]));
            throw new RuntimeException(ILLEGAL_OPERATION_ALLOWED);

        } catch (IllegalMovementException e) {
            assertEquals(e.getReason(), NO_PATH_AVAILABLE);
        }
    }

    @Test
    private void BasicMovement() {
        Position startPos = new Position(this.board.getReadOnlyPhysicalBoard().getPawnPosition(p));
        this.board.executeAction(p, new MovePawnAction(MovementDirection.DOWN));
        this.board.executeAction(p, new MovePawnAction(MovementDirection.LEFT));
        this.board.executeAction(p, new MovePawnAction(MovementDirection.RIGHT));
        this.board.executeAction(p, new MovePawnAction(MovementDirection.UP));

        assertEquals(this.board.getReadOnlyPhysicalBoard().getPawnPosition(p), startPos);
    }

    @Test
    private void BlockedMovement(){
        // Expecting the pawn to be on (1,4)
        @SuppressWarnings("all")
        Pair<Wall,MovementDirection>[] blockers = new Pair[] {
                Pair.of(new Wall(new Position(1,4), WallDirection.DOWN), MovementDirection.RIGHT),
                Pair.of(new Wall(new Position(0,4), WallDirection.DOWN), MovementDirection.RIGHT),
                Pair.of(new Wall(new Position(1,3), WallDirection.DOWN), MovementDirection.LEFT),
                Pair.of(new Wall(new Position(0,3), WallDirection.DOWN), MovementDirection.LEFT),
                Pair.of(new Wall(new Position(1,4), WallDirection.RIGHT), MovementDirection.DOWN),
                Pair.of(new Wall(new Position(1,3), WallDirection.RIGHT), MovementDirection.DOWN),
                Pair.of(new Wall(new Position(0,3), WallDirection.RIGHT), MovementDirection.UP),
                Pair.of(new Wall(new Position(0,4), WallDirection.RIGHT), MovementDirection.UP),
        };

        for (Pair<Wall, MovementDirection> block : blockers) {
            this.resetBoard();
            this.board.executeAction(p, new MovePawnAction(MovementDirection.DOWN)); // Moving the pawn to (1,4)
            this.board.executeAction(p, new PlaceWallAction(block.getFirst()));
            try {
                this.board.executeAction(p, new MovePawnAction(block.getSecond()));
                throw new RuntimeException(ILLEGAL_OPERATION_ALLOWED);
            } catch (IllegalMovementException e) {
                assertEquals(e.getReason(), MOVING_IN_DIRECTION_IS_NOT_ALLOWED);
            }
        }
    }

    @Test
    private void MoveOutsideBounds() {
        try {
            this.board.executeAction(p, new MovePawnAction(MovementDirection.UP));
            throw new RuntimeException(ILLEGAL_OPERATION_ALLOWED);
        } catch (IllegalMovementException e) {
            assertEquals(e.getReason(), MOVING_IN_DIRECTION_IS_NOT_ALLOWED);
        }
    }

    @Test
    @SuppressWarnings("all")
    private void MoveJump() {
        // Moving the pawn to (7,4)
        for (int i = 0; i < 7; i++) {
            this.board.executeAction(p, new MovePawnAction(MovementDirection.DOWN));
        }
        Pawn other = this.board.getReadOnlyPhysicalBoard().getPawns().stream().filter(x -> !p.equals(x)).findFirst().get(); // Location should be (8,4)
        this.board.executeAction(other, new MovePawnAction(MovementDirection.UP));
        assertEquals(this.board.getReadOnlyPhysicalBoard().getPawnPosition(other), new Position(6,4));
    }

    @Test
    @SuppressWarnings("all")
    private void MoveJumpWallBlockBefore() {
        // Moving the pawn to (7,4)
        for (int i = 0; i < 7; i++) {
            this.board.executeAction(p, new MovePawnAction(MovementDirection.DOWN));
        }
        Pawn other = this.board.getReadOnlyPhysicalBoard().getPawns().stream().filter(x -> !p.equals(x)).findFirst().get(); // Location should be (8,4)
        this.board.executeAction(p, new PlaceWallAction(new Wall(new Position(7,4), WallDirection.RIGHT)));
        try {
            this.board.executeAction(other, new MovePawnAction(MovementDirection.UP));
            throw new RuntimeException(ILLEGAL_OPERATION_ALLOWED);
        } catch (IllegalMovementException e) {
            assertEquals(e.getReason(), MOVING_IN_DIRECTION_IS_NOT_ALLOWED);
        }
    }

    @Test
    @SuppressWarnings("all")
    private void MoveJumpWallBlockAfter() {
        // Moving the pawn to (7,4)
        for (int i = 0; i < 7; i++) {
            this.board.executeAction(p, new MovePawnAction(MovementDirection.DOWN));
        }
        Pawn other = this.board.getReadOnlyPhysicalBoard().getPawns().stream().filter(x -> !p.equals(x)).findFirst().get(); // Location should be (8,4)
        this.board.executeAction(p, new PlaceWallAction(new Wall(new Position(7,4), WallDirection.RIGHT)));
        try {
            this.board.executeAction(other, new MovePawnAction(MovementDirection.UP));
            throw new RuntimeException(ILLEGAL_OPERATION_ALLOWED);
        } catch (IllegalMovementException e) {
            assertEquals(e.getReason(), MOVING_IN_DIRECTION_IS_NOT_ALLOWED);
        }
    }


}
