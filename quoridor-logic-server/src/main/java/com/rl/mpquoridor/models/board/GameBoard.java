package com.rl.mpquoridor.models.board;

import com.rl.mpquoridor.exceptions.IllegalMovementException;
import com.rl.mpquoridor.models.Pawn;
import com.rl.mpquoridor.models.Position;
import com.rl.mpquoridor.models.Wall;
import com.rl.mpquoridor.models.actions.MovePawn;
import com.rl.mpquoridor.models.actions.PlaceWall;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.enums.MovementDirection;
import com.rl.mpquoridor.models.enums.WallDirection;

import java.util.*;

public class GameBoard {
    private final PhysicalBoard board;
    private ReadOnlyPhysicalBoard readOnlyPhysicalBoard;
    private final Map<Pawn, HashSet<Position>> pawnEndLine;
    private Pawn winner = null;

    public GameBoard(int numberOfPlayers, int numberOfWallsPerPlayer) {
        Map<Pawn, Position> pawns;
        this.pawnEndLine = new HashMap<>();

        pawns = switch (numberOfPlayers) {
            case 2 -> initiateTwoPlayers();
            case 3 -> initiateThreePlayers();
            case 4 -> initiateFourPlayers();
            default -> throw new RuntimeException("Invalid number of players");
        };

        this.board = new PhysicalBoard(pawns, numberOfWallsPerPlayer);
    }

    private Map<Pawn, Position> initiateTwoPlayers() {
        Map<Pawn, Position> ret = new HashMap<>();
        Pawn p1 = new Pawn();
        Pawn p2 = new Pawn();
        ret.put(p1, new Position(0, this.getPhysicalBoard().getSize() / 2));
        ret.put(p2, new Position(this.getPhysicalBoard().getSize() - 1, this.getPhysicalBoard().getSize() / 2));
        this.pawnEndLine.put(p1, new HashSet<>());
        this.pawnEndLine.put(p2, new HashSet<>());

        for (int i = 0; i < this.getPhysicalBoard().getSize(); i++) {
            this.pawnEndLine.get(p1).add(new Position(this.getPhysicalBoard().getSize() - 1, i));
            this.pawnEndLine.get(p2).add(new Position(0, i));
        }
        return ret;
    }

    private Map<Pawn, Position> initiateThreePlayers() {
        Map<Pawn, Position> ret = initiateTwoPlayers();
        Pawn p3 = new Pawn();
        ret.put(p3, new Position(this.getPhysicalBoard().getSize() / 2, 0));
        this.pawnEndLine.put(p3, new HashSet<>());
        for (int i = 0; i < this.getPhysicalBoard().getSize(); i++) {
            this.pawnEndLine.get(p3).add(new Position(i, this.getPhysicalBoard().getSize() - 1));
        }
        return ret;
    }

    private Map<Pawn, Position> initiateFourPlayers() {
        Map<Pawn, Position> ret = initiateThreePlayers();
        Pawn p4 = new Pawn();
        ret.put(p4, new Position(this.getPhysicalBoard().getSize() / 2, this.getPhysicalBoard().getSize() -1));
        this.pawnEndLine.put(p4, new HashSet<>());
        for (int i = 0; i < this.getPhysicalBoard().getSize(); i++) {
            this.pawnEndLine.get(p4).add(new Position(0, i));
        }
        return  ret;
    }

    /**
     * Moving the pawn to the given direction. It considerate walls and other pawns.
     * If there is a wall or the movement is out side the board it throws an exception
     * If there is a pawn ( or more ) it jumps over it ( or them )
     * @param pawn - the moving pawn
     * @param direction - the moving direction
     * @throws IllegalMovementException - if the movement is illegal
     */
    private void movePawn(Pawn pawn, MovementDirection direction) throws IllegalMovementException {
        Position dest = this.simulateMove(this.getPhysicalBoard().getPawnPosition(pawn), direction);
        if(dest == null) {
            throw new IllegalMovementException("Illegal moving request.");
        }
        this.getPhysicalBoard().movePawn(pawn, dest);
        checkWinner(pawn);
    }

    /**
     * This method checks if the given pawn won the game.
     * If it does, the method stores the pawn on variable winner.
     * @param pawn - the pawn this method checks.
     */
    private void checkWinner(Pawn pawn) {
        if(winner == null && this.pawnEndLine.get(pawn).contains(this.getPhysicalBoard().getPawnPosition(pawn))) {
            this.winner = pawn;
        }
    }

    private void placeWall (Pawn pawn, Wall wall)  throws IllegalMovementException {
        // Make sure the pawn has enough walls
        if(getPhysicalBoard().getPawnWalls().get(pawn) == 0) {
            throw new IllegalMovementException("No more walls left");
        }

        // Make sure the wall doesn't collides with any other wall
        if(this.isWallCollides(wall)) {
            throw new IllegalMovementException("The wall collides with other wall");
        }

        // Checking the wall is in the bounds of the board
        if(wall.getPosition().getI() < 0 || wall.getPosition().getI() >= this.getPhysicalBoard().getSize() -1 ||
           wall.getPosition().getJ() < 0 || wall.getPosition().getJ() >= this.getPhysicalBoard().getSize() -1 )  {
            throw new IllegalMovementException("The wall is out side the board bounds");
        }

        // Make sure all the pawns have an available path
        this.getPhysicalBoard().putWall(wall); // Putting this, if pawn has no path, remove this wall.
        if(!isAllPawnsHavePath()) {
            this.getPhysicalBoard().removeWall(wall); // removing the illegal wall.
            throw new IllegalMovementException("Not all pawns have available path to their end");
        }

        this.getPhysicalBoard().reduceWallToPawn(pawn);
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
        return this.readOnlyPhysicalBoard;
    }

    /**
     * Should be an O(1) implementation.
     *  @return the winner pawn or null
     */
    public Pawn getWinner() {
        return this.winner;
    }

    private boolean isPathExists(Position s, HashSet<Position> dest) {
        Queue<Position> q = new LinkedList<>();
        Set<Position> visited = new HashSet<>();
        q.add(s);
        while(!q.isEmpty()) {
            Position curr = q.poll();
            if(dest.contains(curr)) { // This is an O(1) operation
                return true;
            }
            if(!visited.contains(curr)) {
                visited.add(curr);
                q.addAll(getAvailableNeighbors(curr));
            }
        }

        return false;
    }

    // todo: Change this to work with other dataSet
    private List<Position> getAvailableNeighbors(Position p) {
        List<Position> ret = new ArrayList<>();
        // UP
        if(p.getI() > 0) {
            if (!this.getPhysicalBoard().getWalls().contains(new Wall(new Position(p.getI(), p.getJ()), WallDirection.RIGHT)) &&
                    !this.getPhysicalBoard().getWalls().contains(new Wall(new Position(p.getI(), p.getJ() - 1), WallDirection.RIGHT))){
                ret.add(new Position(p.getI() - 1, p.getJ()));
            }
        }
        // DOWN
        if(p.getI() < this.getPhysicalBoard().getSize() - 1) {
            if (!this.getPhysicalBoard().getWalls().contains(new Wall(new Position(p.getI()+1, p.getJ()), WallDirection.RIGHT)) &&
                    !this.getPhysicalBoard().getWalls().contains(new Wall(new Position(p.getI()+1, p.getJ() - 1), WallDirection.RIGHT))){
                ret.add(new Position(p.getI() + 1, p.getJ()));
            }
        }

        // LEFT
        if(p.getJ() > 0) {
            if (!this.getPhysicalBoard().getWalls().contains(new Wall(new Position(p.getI(), p.getJ()), WallDirection.DOWN)) &&
                    !this.getPhysicalBoard().getWalls().contains(new Wall(new Position(p.getI()-1, p.getJ()), WallDirection.DOWN))){
                ret.add(new Position(p.getI(), p.getJ() - 1));
            }
        }

        // RIGHT
        if(p.getJ() < this.getPhysicalBoard().getSize() - 1) {
            if (!this.getPhysicalBoard().getWalls().contains(new Wall(new Position(p.getI(), p.getJ()+1), WallDirection.DOWN)) &&
                    !this.getPhysicalBoard().getWalls().contains(new Wall(new Position(p.getI()-1, p.getJ()+1), WallDirection.DOWN))){
                ret.add(new Position(p.getI(), p.getJ() + 1));
            }
        }

        return ret;
    }

    private boolean isWallCollides(Wall wall) {
        for (Wall w : this.getPhysicalBoard().getWalls()) {
            if(isWallCollides(wall, w)) {
                return true;
            }
        }
        return false;
    }

    private boolean isWallCollides(Wall wall1, Wall wall2) {
        // Two walls are the same wall
        if(wall1.equals(wall2)) {
            return true;
        }
        // two walls have the same pos but different direction
        if(wall1.getPosition().equals(wall2.getPosition())) {
            return false;
        }

        if(wall1.getWallDirection().equals(WallDirection.RIGHT) && wall2.getWallDirection().equals(WallDirection.RIGHT)) {
            return wall1.getPosition().getI() == wall2.getPosition().getI() && Math.abs(wall1.getPosition().getJ() - wall2.getPosition().getJ()) <= 1;
        }
        if(wall1.getWallDirection().equals(WallDirection.DOWN) && wall2.getWallDirection().equals(WallDirection.DOWN)) {
            return wall1.getPosition().getJ() == wall2.getPosition().getJ() && Math.abs(wall1.getPosition().getI() - wall2.getPosition().getI()) <= 1;
        }
        if(wall1.getWallDirection().equals(WallDirection.DOWN) && wall2.getWallDirection().equals(WallDirection.RIGHT)) {
            return wall1.getPosition().getI() == wall2.getPosition().getI() - 1 && wall1.getPosition().getJ() == wall2.getPosition().getJ() + 1;
        }
        // If we are here, the commented statement is true.
        // if(wall1.getWallDirection().equals(WallDirection.RIGHT) && wall2.getWallDirection().equals(WallDirection.DOWN)) {
        return wall1.getPosition().getI() == wall2.getPosition().getI() + 1 && wall1.getPosition().getJ() == wall2.getPosition().getJ() - 1;
        // }

    }

    private boolean isAllPawnsHavePath() {
        for (Pawn p : this.getPhysicalBoard().getPawns()) {
            if(!isPathExists(this.getPhysicalBoard().getPawnPosition(p), this.pawnEndLine.get(p))){
                return false;
            }
        }
        return true;
    }

    // This will use to assist for moving the pawn in different directions


    /**
     *
     * @param s - starting position
     * @param direction - direction to go
     * @return the destination or null if the move is illegal
     */
    private Position simulateMove(Position s, MovementDirection direction) {
        return switch (direction) {
            case UP -> simulateUpMove(s);
            case DOWN -> simulateDownMove(s);
            case LEFT -> simulateLeftMove(s);
            case RIGHT -> simulateRightMove(s);
        };
    }

    private Position simulateUpMove(Position s) {

        if(s.getI() <= 0) {
            return null;
        }

        Wall block1 = new Wall(s, WallDirection.RIGHT);
        Wall block2 = new Wall(new Position(s.getI(), s.getJ() - 1), WallDirection.RIGHT);

        if(this.getPhysicalBoard().getWalls().contains(block1) ||
           this.getPhysicalBoard().getWalls().contains(block2)) {
            return null;
        }

        Position next = new Position(s.getI()-1, s.getJ());

        if(this.getPhysicalBoard().pawnAt(next) != null) {
            return simulateUpMove(next);
        } else {
            return next;
        }

    }
    private Position simulateDownMove(Position s) {
        if(s.getI() >= this.getPhysicalBoard().getSize() - 1) {
            return null;
        }

        Position next = new Position(s.getI()+1, s.getJ());

        Wall block1 = new Wall(next, WallDirection.RIGHT);
        Wall block2 = new Wall(new Position(next.getI(), next.getJ() - 1), WallDirection.RIGHT);

        if(this.getPhysicalBoard().getWalls().contains(block1) ||
                this.getPhysicalBoard().getWalls().contains(block2)) {
            return null;
        }

        if(this.getPhysicalBoard().pawnAt(next) != null) {
            return simulateDownMove(next);
        } else {
            return next;
        }
    }
    private Position simulateLeftMove(Position s) {
        if(s.getJ() <= 0) {
            return null;
        }

        Wall block1 = new Wall(s, WallDirection.DOWN);
        Wall block2 = new Wall(new Position(s.getI() - 1, s.getJ()), WallDirection.DOWN);

        if(this.getPhysicalBoard().getWalls().contains(block1) ||
           this.getPhysicalBoard().getWalls().contains(block2)) {
            return null;
        }

        Position next = new Position(s.getI(), s.getJ() - 1);

        if(this.getPhysicalBoard().pawnAt(next) != null) {
            return simulateLeftMove(next);
        } else {
            return next;
        }
    }
    private Position simulateRightMove(Position s) {
        if(s.getJ() >= this.getPhysicalBoard().getSize() - 1) {
            return null;
        }

        Position next = new Position(s.getI(), s.getJ() + 1);

        Wall block1 = new Wall(next, WallDirection.RIGHT);
        Wall block2 = new Wall(new Position(next.getI() - 1 , next.getJ()), WallDirection.DOWN);

        if(this.getPhysicalBoard().getWalls().contains(block1) ||
           this.getPhysicalBoard().getWalls().contains(block2)) {
            return null;
        }

        if(this.getPhysicalBoard().pawnAt(next) != null) {
            return simulateRightMove(next);
        } else {
            return next;
        }
    }
}
