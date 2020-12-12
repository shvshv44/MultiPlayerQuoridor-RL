package com.rl.mpquoridor.models;

import com.rl.mpquoridor.enums.Direction;
import lombok.Data;

@Data
public class Pawn {
    private String color;
    private String name;
    private Position position;
    private Wall[] walls;
    private int numOfWallsStarting;
    private int numOfWallsInUse;
    private int heightWin;

    public Pawn(String color, String name, Position position, int numOfWalls, int heightWin) {
        this.color = color;
        this.name = name;
        this.position = position;
        this.numOfWallsStarting = numOfWalls;
        this.walls = new Wall[10];
        this.numOfWallsInUse = 0;
        this.heightWin = heightWin;
    }

    public void play(){
        //Todo: add wall or move position
    }

    public int numOfWallsRemaining(){
        return numOfWallsStarting - numOfWallsInUse;
    }

    public Wall[] addWall(Wall wall) {
        if (walls.length < numOfWallsStarting) {
            walls[numOfWallsInUse - 1] = wall;
        }
        return walls;
    }

    public void changePositionByDirection(Direction direction){
        //Todo: check if this is a valid new position
        this.position.moveByDirection(direction);
    }

    public boolean isWin() {
        return position.getY() == heightWin;
    }

}
