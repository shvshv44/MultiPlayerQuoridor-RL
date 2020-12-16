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

        switch (numberOfPlayers) {
            case 2:
                pawns = initiateTwoPlayers();
                break;
            case 3:
                pawns = initiateThreePlayers();
                break;
            case 4:
                pawns = initiateFourPlayers();
                break;
            default:
                throw new RuntimeException("Invalid number of players");
        }

        this.board = new PhysicalBoard(pawns, 10);
        this.pawnEndLine = new HashMap<>();
        System.out.println("not yet implemented");
    }

    private Map<Pawn, Position> initiateTwoPlayers() {
        Map<Pawn, Position> ret = new HashMap<>();
        Pawn p1 = new Pawn();
        Pawn p2 = new Pawn();
        ret.put(p1, new Position(0, 4));
        ret.put(p2, new Position(8, 4));
        this.pawnEndLine.put(p1, new HashSet<>());
        this.pawnEndLine.put(p2, new HashSet<>());

        for (int i = 0; i < 9; i++) {
            this.pawnEndLine.get(p1).add(new Position(8, i));
            this.pawnEndLine.get(p2).add(new Position(0, i));
        }
        return ret;
    }

    private Map<Pawn, Position> initiateThreePlayers() {
        Map<Pawn, Position> ret = initiateTwoPlayers();
        Pawn p3 = new Pawn();
        ret.put(p3, new Position(4, 0));
        this.pawnEndLine.put(p3, new HashSet<>());
        for (int i = 0; i < 9; i++) {
            this.pawnEndLine.get(p3).add(new Position(i, 8));
        }
        return ret;
    }

    private Map<Pawn, Position> initiateFourPlayers() {
        Map<Pawn, Position> ret = initiateThreePlayers();
        Pawn p4 = new Pawn();
        ret.put(p4, new Position(4, 8));
        this.pawnEndLine.put(p4, new HashSet<>());
        for (int i = 0; i < 9; i++) {
            this.pawnEndLine.get(p4).add(new Position(0, i));
        }
        return  ret;
    }

    private void movePawn(Pawn pawn, MovementDirection direction) throws IllegalMovementException {
        // todo: implement this method in the right way...
        System.out.println("not yet implemented");

        Position dest = new Position(this.board.getPawns().get(pawn));

        if(direction.equals(MovementDirection.UP)) {
            dest.setJ(dest.getI() - 1);
        } else if (direction.equals(MovementDirection.DOWN)) {
            dest.setJ(dest.getI() + 1);
        } else if (direction.equals(MovementDirection.LEFT)) {
            dest.setJ(dest.getJ() - 1);
        } else if (direction.equals(MovementDirection.RIGHT)) {
            dest.setJ(dest.getJ() + 1);
        }
        this.board.movePawn(pawn, this.board.getPawns().get(pawn),dest);

        checkWinner(pawn);
    }

    private void checkWinner(Pawn pawn) {
        if(winner == null && this.pawnEndLine.get(pawn).contains(this.getPhysicalBoard().getPawns().get(pawn))) {
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
        if(wall.getPos().getI() < 0 || wall.getPos().getI() >= this.getPhysicalBoard().getSize() -1 ||
           wall.getPos().getJ() < 0 || wall.getPos().getJ() >= this.getPhysicalBoard().getSize() -1 )  {
            throw new IllegalMovementException("The wall is out side the board bounds");
        }

        // Make sure all the pawns have an available path
        this.getPhysicalBoard().putWall(wall);
        boolean isAllPathExists = true;
        for (Pawn p : this.getPhysicalBoard().getPawns().keySet()) {
            if(!isPathExists(this.getPhysicalBoard().getPawns().get(p), this.pawnEndLine.get(p))){
                isAllPathExists = false;
                break;
            }
        }
        if(!isAllPathExists) {
            this.getPhysicalBoard().removeWall(wall);
            throw new IllegalMovementException("Not all pawns have access to their finish line");
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

    // todo: implement this methods
    private boolean isWallCollides(Wall wall) {
        return false;
    }
}
