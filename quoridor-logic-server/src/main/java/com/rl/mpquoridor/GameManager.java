package com.rl.mpquoridor;

import com.rl.mpquoridor.exceptions.IllegalMovementException;
import com.rl.mpquoridor.models.Pawn;
import com.rl.mpquoridor.models.actions.PlaceWall;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.GameBoard;
import com.rl.mpquoridor.models.players.Player;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.google.common.collect.*;
import java.util.List;


public class GameManager {
    GameBoard gameBoard;
    Player[] players; // Ordered by their turns (players[0] play first, then players[1]....)
    BiMap<Player, Pawn> playerPawn;
    int currentTurn;
    public GameManager(Player[] players, int numberOfWallsPerPlayer) {
        this.players = players.clone();
        this.gameBoard = new GameBoard(this.players.length, numberOfWallsPerPlayer);
        this.playerPawn =  HashBiMap.create(this.players.length);
        int i = 0;
        for(Pawn p : this.gameBoard.getPhysicalBoard().getPawns().keySet()) {
            this.players[i].setMyPawn(p);
            this.playerPawn.put(this.players[i], p);
            i++;
        }
    }

    /**
     * @return the winner and all the turns taken in the game.
     */
    public Pair<Player, List<Pair<Player, TurnAction>>> run() {
        List<Pair<Player, TurnAction>> history = new ArrayList<>();
        while(this.gameBoard.winner() == null) {
            Player currentPlayer = this.players[currentTurn];
            TurnAction action = currentPlayer.play();
            try {
                this.gameBoard.executeAction(this.playerPawn.get(currentPlayer), action);
                Arrays.stream(players).forEach(Player::triggerChange);
                history.add(Pair.of(currentPlayer, action));
                currentTurn = (currentTurn + 1) % this.players.length;

            } catch (IllegalMovementException e) {
                currentPlayer.illegalMovePlayed();
            }
        }

        return Pair.of(playerPawn.inverse().get(gameBoard.winner()), history);
    }
}

