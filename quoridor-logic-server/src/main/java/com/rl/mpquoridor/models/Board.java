package com.rl.mpquoridor.models;

import com.rl.mpquoridor.enums.Direction;
import lombok.Data;

@Data
public class Board {
    private int[][] board;

    public Board(int size) {
        int newSize = size * 2 - 1;
        this.board = new int[newSize][newSize];
        this.initBoard();
    }

    public int[][] addWall(Wall wall) {
        Direction direction = wall.getDirection();
        Position position = wall.getPosition();
        int newX = position.getX() * 2 + 1;
        int newY = position.getY() * 2 + 1;
        if (direction == Direction.Right) {
            this.board[newY][newX] = 1;
            this.board[newY][newX + 1] = 1;
            this.board[newY][newX + 2] = 1;
        } else {
            this.board[newY][newX] = 1;
            this.board[newY + 1][newX] = 1;
            this.board[newY + 2][newX] = 1;
        }

        return this.board;
    }

    public boolean checkAddingWall(Wall wall) {
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

        //Todo: check if there still option to play


        return true;
    }

    private void initBoard() {
        for (int height = 0; height < this.board.length; height++) {
            for (int width = 0; width < this.board[0].length; width++) {
                this.board[height][width] = 0;
            }
        }
    }
}
