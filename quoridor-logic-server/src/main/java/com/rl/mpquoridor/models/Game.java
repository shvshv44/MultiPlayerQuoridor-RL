package com.rl.mpquoridor.models;

public class Game {
    private Pawn player1;
    private Pawn player2;
    private Pawn Player3;
    private Pawn Player4;
    private Pawn[] playersOrder;
    private int playerTurn;
    private Board board;

    public Game() {
        int numOfWalls = 10;
        boolean finishGame = false;
        playerTurn = 0;

        // init players
        this.player1 = new Pawn(1, "red", "shaorn", new Position(4, 0), numOfWalls, 8);
        this.player2 = new Pawn(2, "Blue", "Shaked", new Position(4, 8), numOfWalls, 0);
        this.playersOrder = new Pawn[]{this.player1, this.player2};
        Position[] pawnPosition = new Position[this.playersOrder.length];
        for (int indexPlayer = 0; indexPlayer < this.playersOrder.length; indexPlayer++) {
            pawnPosition[indexPlayer] = this.playersOrder[indexPlayer].getPosition();
        }

        this.board = new Board(9, pawnPosition);

        this.board.printBoard();

        while (!finishGame) {
            //TODO: publish player turn
            System.out.println(this.playersOrder[this.playerTurn].getName() + this.playersOrder[this.playerTurn].getId() + " - Its your turn");

            int[] heightWin = new int[this.playersOrder.length];
            for (int indexPlayer = 0; indexPlayer < this.playersOrder.length; indexPlayer++) {
                pawnPosition[indexPlayer] = this.playersOrder[indexPlayer].getPosition();
                heightWin[indexPlayer] = this.playersOrder[indexPlayer].getHeightWin();
            }
            Pawn player = playersOrder[playerTurn];
            player.play(this.board, pawnPosition, heightWin);

            //TODO: publish the new game
            this.board.printBoard();


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
