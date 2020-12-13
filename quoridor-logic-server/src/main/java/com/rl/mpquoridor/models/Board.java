package com.rl.mpquoridor.models;

import com.rl.mpquoridor.enums.Cell;
import com.rl.mpquoridor.enums.Direction;
import lombok.Data;

import java.util.LinkedList;

@Data
public class Board {
    private Cell[][] board;

    public Board(int size, Position[] pawnPositions) {
        int newSize = size * 2 - 1;
        this.board = new Cell[newSize][newSize];
        this.initBoard();

        for (Position position: pawnPositions) {
            this.board[position.getY() * 2][position.getX() * 2] = Cell.PAWN;
        }
    }

    public void printBoard() {
        for (int height = 0; height < this.board.length; height++) {
            System.out.println("");
            for (int width = 0; width < this.board[0].length; width++) {
                System.out.print(this.board[height][width] + " , ");
            }
        }

        System.out.println("");
    }

    public boolean movePawn(Position fromPosition, Position toPosition) {
        //TODO: check if this is a valid new position
        this.board[fromPosition.getY() * 2][fromPosition.getX() * 2] = Cell.EMPTY;
        this.board[toPosition.getY() * 2][toPosition.getX() * 2] = Cell.PAWN;
        return true;
    }

    public boolean addWall(Wall wall, Position[] positions, int[] heightsWin) {
        if (this.checkAddingWall(wall, positions, heightsWin)) {
            this.board = this.addWallToBoard(wall, this.board);
            return true;
        }

        return false;
    }

    public boolean checkAddingWall(Wall wall, Position[] positions, int[] heightsWin) {
        Direction direction = wall.getDirection();
        Position position = wall.getPosition();
        int newX = position.getX() * 2 + 1;
        int newY = position.getY() * 2 + 1;

        if (direction == Direction.Right) {
            if (newX + 2 > this.board[0].length) {
                return false;
            } else {
                if (this.board[newY][newX] == Cell.WALL ||
                        this.board[newY][newX + 1] == Cell.WALL ||
                        this.board[newY][newX + 2] == Cell.WALL) {
                    return false;
                }
            }
        } else {
            if (newY + 2 > this.board.length) {
                return false;
            } else {
                if (this.board[newY][newX] == Cell.WALL ||
                        this.board[newY + 1][newX] == Cell.WALL ||
                        this.board[newY + 2][newX] == Cell.WALL) {
                    return false;
                }
            }
        }

        return this.checkPaths(wall, positions, heightsWin);
    }

    private void initBoard() {
        for (int height = 0; height < this.board.length; height++) {
            for (int width = 0; width < this.board[0].length; width++) {
                this.board[height][width] = Cell.EMPTY;
            }
        }
    }

    private boolean checkPaths(Wall wall, Position[] positions, int[] heightsWin) {
        Cell[][] newBoard = new Cell[this.board.length][this.board[0].length];
        for (int height = 0; height < this.board.length; height++) {
            System.arraycopy(this.board[height], 0, newBoard[height], 0, this.board[0].length);
        }

        newBoard = this.addWallToBoard(wall, newBoard);

        for (int i = 0; i < positions.length; i++) {
            if (this.checkingExistingPath(newBoard, positions[i], heightsWin[i])) {
                return true;
            }
        }
        return false;
    }

    //BFS
    // 1 - checked
    // 0 - not checked
    private boolean checkingExistingPath(Cell[][] board, Position position, int rawExit) {
        LinkedList<Position> nextToVisit
                = new LinkedList<>();
        nextToVisit.add(position);

        while (!nextToVisit.isEmpty()) {
            Position cur = nextToVisit.remove();

            if (!this.isValidLocation(cur)
                    || board[cur.getY()][cur.getX()] == Cell.CHECKED) {
                continue;
            }

            // Exit - need to change for four players
            if (cur.getY() == rawExit) {
                return true;
            }

            //RIGHT
            if (cur.getY() != this.board.length + 1 && board[cur.getY()][cur.getX() + 1] != Cell.WALL) {
                Position Position
                        = new Position(
                        cur.getX() + 2,
                        cur.getY());
                nextToVisit.add(Position);
            }
            // LEFT
            if (cur.getX() != 0 && board[cur.getY()][cur.getX() - 1] != Cell.WALL) {
                Position Position
                        = new Position(
                        cur.getX() - 2,
                        cur.getY());
                nextToVisit.add(Position);
            }
            // UP
            if (cur.getY() != this.board.length - 1 && board[cur.getY() + 1][cur.getX()] != Cell.WALL) {
                Position Position
                        = new Position(
                        cur.getX(),
                        cur.getY() + 2);
                nextToVisit.add(Position);
            }
            // DOWN
            if (cur.getY() != 0 && board[cur.getY() - 1][cur.getX()] != Cell.WALL) {
                Position Position
                        = new Position(
                        cur.getX(),
                        cur.getY() - 2);
                nextToVisit.add(Position);
            }
            board[cur.getY()][cur.getX()] = Cell.CHECKED;
        }
        return true;
    }

    public Cell[][] addWallToBoard(Wall wall, Cell[][] board) {
        Direction direction = wall.getDirection();
        Position position = wall.getPosition();
        int newX = position.getX() * 2;
        int newY = position.getY() * 2;
        if (direction == Direction.Right) {
            board[newY + 1][newX] = Cell.WALL;
            board[newY + 1][newX + 1] = Cell.WALL;
            board[newY + 1][newX + 2] = Cell.WALL;
        } else {
            board[newY][newX + 1] = Cell.WALL;
            board[newY + 1][newX + 1] = Cell.WALL;
            board[newY + 2][newX + 1] = Cell.WALL;
        }

        return board;
    }

    private boolean isValidLocation(Position position) {
        int size = this.board.length;
        int x = position.getX();
        int y = position.getY();
        return x >= 0 && x < size && y >= 0 && y < size;
    }
}
