package com.rl.mpquoridor.models;

import com.rl.mpquoridor.enums.Direction;
import lombok.Data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

@Data
public class Pawn {
    private int id;
    private String color;
    private String name;
    private Position position;
    private Wall[] walls;
    private int numOfWallsStarting;
    private int numOfWallsInUse;
    private int heightWin;

    public Pawn(int id, String color, String name, Position position, int numOfWalls, int heightWin) {
        this.id = id;
        this.color = color;
        this.name = name;
        this.position = position;
        this.numOfWallsStarting = numOfWalls;
        this.walls = new Wall[10];
        this.numOfWallsInUse = 0;
        this.heightWin = heightWin;
    }

    public void play(Board board,  Position[] positions, int[] heightsWin) {
        //Todo: change to pubsub
        Boolean validInput = false;

        Scanner in = new Scanner(System.in);

        while (!validInput) {
            System.out.println("For moving press 1 otherwise press 2 ");
            String option =  in.nextLine();
            if (option.equals("1")) {
                System.out.println("Choose direction r, l , u , d");
                String direction =  in.nextLine();
                switch (direction) {
                    case "r":
                        this.changePositionByDirection(board, Direction.Right);
                        break;
                    case "l":
                        this.changePositionByDirection(board, Direction.Left);
                        break;
                    case "d":
                        this.changePositionByDirection(board, Direction.Down);
                        break;
                    default:
                        this.changePositionByDirection(board, Direction.Up);
                        break;
                }
                validInput = true;
            } else {
                System.out.println("Choose where to put the wall");
                System.out.println("x - ");
                String x =  in.nextLine();
                System.out.println("y - ");
                String y = in.nextLine();
                System.out.println("direction r - right, d - down");
                String consoleDirection =  in.nextLine();
                Direction direction;
                if (consoleDirection.equals("r")) {
                    direction = Direction.Right;
                } else {
                    direction = Direction.Down;
                }
                Position position = new Position(Integer.parseInt(x), Integer.parseInt(y));
                Wall wall = new Wall(position, direction);
                if (board.addWall(wall, positions, heightsWin)) {
                    this.addWall(wall);
                    validInput = true;
                }
            }
        }

    }

    public int numOfWallsRemaining() {
        return numOfWallsStarting - numOfWallsInUse;
    }

    public Wall[] addWall(Wall wall) {
        if (walls.length < numOfWallsStarting) {
            walls[numOfWallsInUse - 1] = wall;
        }
        return walls;
    }

    public boolean changePositionByDirection(Board board, Direction direction) {
        Position newPosition = new Position(this.position.getX(), this.position.getY());
        newPosition.moveByDirection(direction);
        //Todo: check if this is a valid new position
        if (board.movePawn(this.position, newPosition)) {
            this.position.moveByDirection(direction);
            return true;
        }

        return false;
    }

    public boolean isWin() {
        return position.getY() == heightWin;
    }

}
