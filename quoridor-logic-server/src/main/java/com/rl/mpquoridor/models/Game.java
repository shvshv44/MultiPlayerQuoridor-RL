package com.rl.mpquoridor.models;

public class Game {
    private Pawn player1;
    private Pawn player2;
    private Pawn Player3;
    private Pawn Player4;
    private Pawn[] playersOrder;
    private int playerTurn;

    public Game() {
        int numOfWalls = 10;
        boolean finishGame = false;
        playerTurn = 0;

        // init players
        this.player1 = new Pawn("red", "shaorn", new Position(4, 0), numOfWalls, 8);
        this.player2 = new Pawn("Blue", "Shaked", new Position(4, 8), numOfWalls, 0);
        this.playersOrder = new Pawn[]{this.player1, this.player2};

        while (!finishGame) {
            Pawn player = playersOrder[playerTurn];
            player.play();

            //publish new game

            if (player.isWin()) {
                finishGame = true;
                //publish the winner and finish game
            } else {
                this.playerTurn = (this.playerTurn + 1) % this.playersOrder.length;
            }

        }

        System.out.println("The winner is " + playersOrder[playerTurn].getName());
    }

}
