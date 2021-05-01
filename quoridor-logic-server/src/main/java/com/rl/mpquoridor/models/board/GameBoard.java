package com.rl.mpquoridor.models.board;

import com.rl.mpquoridor.exceptions.IllegalMovementException;
import com.rl.mpquoridor.models.actions.MovePawnAction;
import com.rl.mpquoridor.models.actions.PlaceWallAction;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.enums.MovementDirection;
import com.rl.mpquoridor.models.enums.WallDirection;
import lombok.Getter;

import java.util.*;

import static com.rl.mpquoridor.exceptions.IllegalMovementException.Reason.*;

public class GameBoard {
    private final PhysicalBoard board;
    private ReadOnlyPhysicalBoard readOnlyPhysicalBoard;
    @Getter
    private Pawn winner = null;
    private final List<Pawn> playOrder = new LinkedList<>();


    public GameBoard(Map<Pawn, Position> pawns,
                     Map<Pawn, Integer> pawnWalls,
                     List<Pawn> playOrder,
                     Pawn currentTurn,
                     Set<Wall> walls,
                     Map<Pawn, Set<Position>> pawnEndLine
                     ) {
        this.board = new PhysicalBoard();
        this.board.setPawnPosition(pawns);
        this.board.setPawnEndLine(pawnEndLine);
        walls.forEach(this.board::putWall);
        this.board.setPawnWalls(pawnWalls);
        while(playOrder.get(0) != currentTurn) {
            playOrder.add(playOrder.remove(0));
        }
    }

    public GameBoard(int numberOfPlayers, int numberOfWallsPerPlayer) {
        Map<Pawn, Position> pawns;
        this.board = new PhysicalBoard(numberOfWallsPerPlayer);
        this.board.setPawnEndLine(new HashMap<>());

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

        this.board.setPawnPosition(pawns);

    }

    private Map<Pawn, Position> initiateTwoPlayers() {
        Map<Pawn, Position> ret = new HashMap<>();
        Pawn p1 = new Pawn();
        Pawn p2 = new Pawn();

        playOrder.add(p1);
        playOrder.add(p2);

        ret.put(p1, new Position(0, this.getPhysicalBoard().getSize() / 2));
        ret.put(p2, new Position(this.getPhysicalBoard().getSize() - 1, this.getPhysicalBoard().getSize() / 2));
        this.board.getPawnEndLine().put(p1, new HashSet<>());
        this.board.getPawnEndLine().put(p2, new HashSet<>());

        for (int i = 0; i < this.getPhysicalBoard().getSize(); i++) {
            this.board.getPawnEndLine().get(p1).add(new Position(this.getPhysicalBoard().getSize() - 1, i));
            this.board.getPawnEndLine().get(p2).add(new Position(0, i));
        }
        return ret;
    }

    private Map<Pawn, Position> initiateThreePlayers() {
        Map<Pawn, Position> ret = initiateTwoPlayers();
        Pawn p3 = new Pawn();
        playOrder.add(p3);
        ret.put(p3, new Position(this.getPhysicalBoard().getSize() / 2, 0));
        this.board.getPawnEndLine().put(p3, new HashSet<>());
        for (int i = 0; i < this.getPhysicalBoard().getSize(); i++) {
            this.board.getPawnEndLine().get(p3).add(new Position(i, this.getPhysicalBoard().getSize() - 1));
        }
        return ret;
    }

    private Map<Pawn, Position> initiateFourPlayers() {
        Map<Pawn, Position> ret = initiateThreePlayers();
        Pawn p4 = new Pawn();
        playOrder.add(p4);
        ret.put(p4, new Position(this.getPhysicalBoard().getSize() / 2, this.getPhysicalBoard().getSize() - 1));
        this.board.getPawnEndLine().put(p4, new HashSet<>());
        for (int i = 0; i < this.getPhysicalBoard().getSize(); i++) {
            this.board.getPawnEndLine().get(p4).add(new Position(i, 0));
        }
        return ret;
    }

    /**
     * Moving the pawn to the given direction. It considerate walls and other pawns.
     * If there is a wall or the movement is out side the board it throws an exception
     * If there is a pawn ( or more ) it jumps over it ( or them )
     *
     * @param pawn      - the moving pawn
     * @param direction - the moving direction
     * @throws IllegalMovementException - if the movement is illegal
     */
    private void movePawn(Pawn pawn, MovementDirection direction) throws IllegalMovementException {
        Position dest = this.simulateMove(this.getPhysicalBoard().getPawnPosition(pawn), direction);
        if (dest == null) {
            throw new IllegalMovementException(MOVING_IN_DIRECTION_IS_NOT_ALLOWED);
        }
        this.getPhysicalBoard().movePawn(pawn, dest);
        checkWinner(pawn);
    }

    /**
     * This method checks if the given pawn won the game.
     * If it does, the method stores the pawn on variable winner.
     *
     * @param pawn - the pawn this method checks.
     */
    private void checkWinner(Pawn pawn) {
        if (winner == null && this.board.getPawnEndLine().get(pawn).contains(this.getPhysicalBoard().getPawnPosition(pawn))) {
            this.winner = pawn;
        }
    }

    private void placeWall(Pawn pawn, Wall wall) throws IllegalMovementException {
        simulatePlaceWall(pawn, wall);
        this.getPhysicalBoard().putWall(wall);
        this.getPhysicalBoard().reduceWallToPawn(pawn);
    }

    private void simulatePlaceWall(Pawn pawn, Wall wall) throws IllegalMovementException {
        // Make sure the pawn has enough walls
        if (getPhysicalBoard().getPawnWalls().get(pawn) == 0) {
            throw new IllegalMovementException(NO_WALLS_LEFT);
        }

        // Make sure the wall doesn't collides with any other wall
        if (this.isWallCollides(wall)) {
            throw new IllegalMovementException(WALL_COLLIDES_WITH_OTHER_WALL);
        }

        // Checking the wall is in the bounds of the board
        if (wall.getPosition().getY() < 0 || wall.getPosition().getY() >= this.getPhysicalBoard().getSize() - 1 ||
                wall.getPosition().getX() < 0 || wall.getPosition().getX() >= this.getPhysicalBoard().getSize() - 1) {
            throw new IllegalMovementException(WALL_IS_OUTSIDE_THE_BOARD_BOUNDS);
        }

        // Make sure all the pawns have an available path
        this.getPhysicalBoard().putWall(wall); // Putting this, if pawn has no path, remove this wall.
        if (!isAllPawnsHavePath()) {
            this.getPhysicalBoard().removeWall(wall);
            throw new IllegalMovementException(NO_PATH_AVAILABLE);
        }
        this.getPhysicalBoard().removeWall(wall);
    }

    public Set<Wall> getAvailableWalls(Pawn pawn) {
        Set<Wall> ret = new HashSet<>();
        for (int i = 0; i < this.getPhysicalBoard().getSize() - 1; i++) {
            for (int j = 0; j < this.getPhysicalBoard().getSize() - 1; j++) {
                for (WallDirection direction : WallDirection.values()) {
                    Wall w = new Wall(new Position(i, j), direction);
                    try {
                        this.simulatePlaceWall(pawn, w);
                        ret.add(w);
                    } catch (IllegalMovementException ignored) {
                    }
                }
            }
        }

        return ret;
    }

    public void executeAction(Pawn pawn, TurnAction action) throws IllegalMovementException {
        if (action instanceof PlaceWallAction) {
            placeWall(pawn, ((PlaceWallAction) action).getWall());
        } else if (action instanceof MovePawnAction) {
            movePawn(pawn, ((MovePawnAction) action).getDirection());
        }
    }


    private PhysicalBoard getPhysicalBoard() {
        return this.board;
    }

    public ReadOnlyPhysicalBoard getReadOnlyPhysicalBoard() {
        if (this.readOnlyPhysicalBoard == null) {
            this.readOnlyPhysicalBoard = new ReadOnlyPhysicalBoard(this.board);
        }
        return this.readOnlyPhysicalBoard;
    }

    private boolean isPathExists(Position s, Set<Position> dest) {
        Queue<Position> q = new LinkedList<>();
        Set<Position> visited = new HashSet<>();
        q.add(s);
        while (!q.isEmpty()) {
            Position curr = q.poll();
            if (dest.contains(curr)) { // This is an O(1) operation
                return true;
            }
            if (!visited.contains(curr)) {
                visited.add(curr);
                q.addAll(getAvailableNeighbors(curr));
            }
        }

        return false;
    }

    private List<Position> getAvailableNeighbors(Position p) {
        List<Position> ret = new LinkedList<>();
        // UP
        if (p.getY() > 0 && Collections.disjoint(this.getPhysicalBoard().getWalls(), getUpBlockers(p))) {
            ret.add(new Position(p.getY() - 1, p.getX()));
        }
        // DOWN
        if (p.getY() < this.getPhysicalBoard().getSize() - 1 && Collections.disjoint(this.getPhysicalBoard().getWalls(), getDownBlockers(p))) {
            ret.add(new Position(p.getY() + 1, p.getX()));
        }

        // LEFT
        if (p.getX() > 0 && Collections.disjoint(this.getPhysicalBoard().getWalls(), getLeftBlockers(p))) {
            ret.add(new Position(p.getY(), p.getX() - 1));
        }

        // RIGHT
        if (p.getX() < this.getPhysicalBoard().getSize() - 1 && Collections.disjoint(this.getPhysicalBoard().getWalls(), getRightBlockers(p))) {
            ret.add(new Position(p.getY(), p.getX() + 1));
        }

        return ret;
    }

    private boolean isWallCollides(Wall wall) {
        for (Wall w : this.getPhysicalBoard().getWalls()) {
            if (isWallCollides(wall, w)) {
                return true;
            }
        }
        return false;
    }

    private boolean isWallCollides(Wall wall1, Wall wall2) {

        if (wall1.getPosition().equals(wall2.getPosition())) {
            return true;
        }

        if (wall1.getWallDirection().equals(WallDirection.RIGHT) && wall2.getWallDirection().equals(WallDirection.RIGHT)) {
            return wall1.getPosition().getY() == wall2.getPosition().getY() && Math.abs(wall1.getPosition().getX() - wall2.getPosition().getX()) <= 1;
        }
        if (wall1.getWallDirection().equals(WallDirection.DOWN) && wall2.getWallDirection().equals(WallDirection.DOWN)) {
            return wall1.getPosition().getX() == wall2.getPosition().getX() && Math.abs(wall1.getPosition().getY() - wall2.getPosition().getY()) <= 1;
        }

        return false;

    }

    private boolean isAllPawnsHavePath() {
        for (Pawn p : this.getPhysicalBoard().getPawns()) {
            if (!isPathExists(this.getPhysicalBoard().getPawnPosition(p), this.board.getPawnEndLine().get(p))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param s         - starting position
     * @param direction - direction to go
     * @return the destination or null if the move is illegal
     */
    private Position simulateMove(Position s, MovementDirection direction) {
        Position position = null;
        switch (direction) {
            case UP:
                position = simulateUpMove(s);
                break;
            case DOWN:
                position = simulateDownMove(s);
                break;
            case LEFT:
                position = simulateLeftMove(s);
                break;
            case RIGHT:
                position = simulateRightMove(s);
                break;
        }

        return position;
    }

    private Position simulateUpMove(Position s) {

        if (s.getY() <= 0) {
            return null;
        }
        Position next = new Position(s.getY() - 1, s.getX());
        List<Wall> blockers = getUpBlockers(s);

        if (!Collections.disjoint(this.getPhysicalBoard().getWalls(), blockers)) {
            return null;
        }

        if (this.getPhysicalBoard().pawnAt(next) != null) {
            return simulateUpMove(next);
        } else {
            return next;
        }

    }

    private Position simulateDownMove(Position s) {
        if (s.getY() >= this.getPhysicalBoard().getSize() - 1) {
            return null;
        }

        Position next = new Position(s.getY() + 1, s.getX());
        List<Wall> blockers = getDownBlockers(s);

        if (!Collections.disjoint(this.getPhysicalBoard().getWalls(), blockers)) {
            return null;
        }

        if (this.getPhysicalBoard().pawnAt(next) != null) {
            return simulateDownMove(next);
        } else {
            return next;
        }
    }

    private Position simulateLeftMove(Position s) {
        if (s.getX() <= 0) {
            return null;
        }

        Position next = new Position(s.getY(), s.getX() - 1);
        List<Wall> blockers = getLeftBlockers(s);
        if (!Collections.disjoint(this.getPhysicalBoard().getWalls(), blockers)) {
            return null;
        }

        if (this.getPhysicalBoard().pawnAt(next) != null) {
            return simulateLeftMove(next);
        } else {
            return next;
        }
    }

    private Position simulateRightMove(Position s) {
        if (s.getX() >= this.getPhysicalBoard().getSize() - 1) {
            return null;
        }

        Position next = new Position(s.getY(), s.getX() + 1);
        List<Wall> blockers = getRightBlockers(s);

        if (!Collections.disjoint(this.getPhysicalBoard().getWalls(), blockers)) {
            return null;
        }

        if (this.getPhysicalBoard().pawnAt(next) != null) {
            return simulateRightMove(next);
        } else {
            return next;
        }
    }

    public List<Pawn> getPlayOrder() {
        return Collections.unmodifiableList(this.playOrder);
    }

    public List<Position> getCurrentPlayerMoves(Pawn pawn) {
        List<Position> currentPlayerMoves = new ArrayList<>();
        Arrays.stream(MovementDirection.values()).forEach(movementDirection -> {
            Position dest = this.simulateMove(this.getPhysicalBoard().getPawnPosition(pawn), movementDirection);
            if (dest != null) {
                currentPlayerMoves.add(dest);
            }
        });

        return currentPlayerMoves;
    }


    private List<Wall> getUpBlockers(Position p) {
        List<Wall> ret = new LinkedList<>();
        ret.add(new Wall(new Position(p.getY() - 1, p.getX() - 1), WallDirection.RIGHT));
        ret.add(new Wall(new Position(p.getY() - 1, p.getX()), WallDirection.RIGHT));
        return ret;
    }

    private List<Wall> getDownBlockers(Position p) {
        List<Wall> ret = new LinkedList<>();
        ret.add(new Wall(new Position(p.getY(), p.getX()), WallDirection.RIGHT));
        ret.add(new Wall(new Position(p.getY(), p.getX() - 1), WallDirection.RIGHT));
        return ret;
    }

    private List<Wall> getLeftBlockers(Position p) {
        List<Wall> ret = new LinkedList<>();
        ret.add(new Wall(new Position(p.getY() - 1, p.getX() - 1), WallDirection.DOWN));
        ret.add(new Wall(new Position(p.getY(), p.getX() - 1), WallDirection.DOWN));
        return ret;
    }

    private List<Wall> getRightBlockers(Position p) {
        List<Wall> ret = new LinkedList<>();
        ret.add(new Wall(new Position(p.getY() - 1, p.getX()), WallDirection.DOWN));
        ret.add(new Wall(new Position(p.getY(), p.getX()), WallDirection.DOWN));
        return ret;
    }
}
