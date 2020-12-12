package com.rl.mpquoridor.models;

import com.rl.mpquoridor.enums.Direction;
import lombok.Data;

import java.util.LinkedList;

@Data
public class Board {
    private int[][] board;

    public Board(int size) {
        int newSize = size * 2 - 1;
        this.board = new int[newSize][newSize];
        this.initBoard();
    }

    public int[][] addWall(Wall wall) {
        this.board = this.addWallToBoard(wall, this.board);
        return this.board;
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
                if (this.board[newY][newX] == 1 ||
                        this.board[newY][newX + 1] == 1 ||
                        this.board[newY][newX + 2] == 1) {
                    return false;
                }
            }
        } else {
            if (newY + 2 > this.board.length) {
                return false;
            } else {
                if (this.board[newY][newX] == 1 ||
                        this.board[newY + 1][newX] == 1 ||
                        this.board[newY + 2][newX] == 1) {
                    return false;
                }
            }
        }

        return this.checkPaths(wall, positions, heightsWin);
    }

    private void initBoard() {
        for (int height = 0; height < this.board.length; height++) {
            for (int width = 0; width < this.board[0].length; width++) {
                this.board[height][width] = 0;
            }
        }
    }

    private boolean checkPaths(Wall wall, Position[] positions, int[] heightsWin) {
        int[][] newBoard = new int[this.board.length][this.board[0].length];
        for (int height = 0; height < this.board.length; height++) {
            System.arraycopy(this.board[height], 0, newBoard[height], 0, this.board[0].length);
        }

        newBoard = this.addWallToBoard(wall, newBoard);

        for (int i = 0; i < positions.length; i++) {
            if (this.checkingExistingPath(newBoard, positions[i], heightsWin[i])) {
                return false;
            }
        }
        return true;
    }

    //BFS
    // 1 - checked
    // 0 - not checked
    private boolean checkingExistingPath(int[][] board, Position position, int rawExit) {
        int explored = 1;

        LinkedList<Position> nextToVisit
                = new LinkedList<>();
        nextToVisit.add(position);

        while (!nextToVisit.isEmpty()) {
            Position cur = nextToVisit.remove();

            if (!this.isValidLocation(cur)
                    || board[cur.getY()][cur.getX()] == explored) {
                continue;
            }

            // Exit - need to change for four players
            if (cur.getY() == rawExit) {
                return true;
            }

            //RIGHT
            if (board[cur.getY()][cur.getX() + 1] != 1) {
                Position Position
                        = new Position(
                        cur.getX() + 2,
                        cur.getY());
                nextToVisit.add(Position);
            }
            // LEFT
            else if (board[cur.getY()][cur.getX() - 1] != 1) {
                Position Position
                        = new Position(
                        cur.getX() - 2,
                        cur.getY());
                nextToVisit.add(Position);
            }
            // UP
            else if (board[cur.getY() + 1][cur.getX()] != 1) {
                Position Position
                        = new Position(
                        cur.getX(),
                        cur.getY() + 2);
                nextToVisit.add(Position);
            }
            // DOWN
            else if (board[cur.getY() - 1][cur.getX()] != 1) {
                Position Position
                        = new Position(
                        cur.getX(),
                        cur.getY() - 2);
                nextToVisit.add(Position);
            }
            board[cur.getY()][cur.getX()] = 2;
        }
        return true;
    }

    public int[][] addWallToBoard(Wall wall, int[][] board) {
        Direction direction = wall.getDirection();
        Position position = wall.getPosition();
        int newX = position.getX() * 2 + 1;
        int newY = position.getY() * 2 + 1;
        if (direction == Direction.Right) {
            board[newY][newX] = 1;
            board[newY][newX + 1] = 1;
            board[newY][newX + 2] = 1;
        } else {
            board[newY][newX] = 1;
            board[newY + 1][newX] = 1;
            board[newY + 2][newX] = 1;
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
