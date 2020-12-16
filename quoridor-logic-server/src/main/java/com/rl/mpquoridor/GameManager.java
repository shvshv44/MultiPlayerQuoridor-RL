package com.rl.mpquoridor;

import com.rl.mpquoridor.exceptions.IllegalMovementException;
import com.rl.mpquoridor.models.GameResult;
import com.rl.mpquoridor.models.Pawn;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.GameBoard;
import com.rl.mpquoridor.models.players.Player;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import com.google.common.collect.*;
import java.util.List;


public class GameManager {
    GameBoard gameBoard;
    Player[] players; // Ordered by their turns (players[0] play first, then players[1]....)
    BiMap<Player, Pawn> playerPawn;
    int currentTurn;
    public GameManager(Player[] players, int numberOfWallsPerPlayer) {
        this.players = new Player[players.length];
        System.arraycopy(players, 0, this.players, 0, players.length);
        this.gameBoard = new GameBoard(this.players.length, numberOfWallsPerPlayer);
        Pawn[] playOrder = (Pawn[]) Arrays.stream(this.players).map(p -> playerPawn.get(p)).toArray();
        this.playerPawn =  HashBiMap.create(this.players.length);
        int i = 0;
        for(Pawn p : this.gameBoard.getPhysicalBoard().getPawns().keySet()) {
            this.players[i].setMyPawn(p);
            this.players[i].setPlayOrder(playOrder);
            this.playerPawn.put(this.players[i], p);
            i++;
        }
    }

    public GameResult run() {
        List<Pair<Player, TurnAction>> history = new ArrayList<>();
        while(this.gameBoard.getWinner() == null) {
            Player currentPlayer = this.players[currentTurn];
            TurnAction action = currentPlayer.play();
            try {
                this.gameBoard.executeAction(this.playerPawn.get(currentPlayer), action);

            } catch (IllegalMovementException e) {
                currentPlayer.illegalMovePlayed(e.getMessage());
                continue;
            }
            history.add(Pair.of(currentPlayer, action));
            currentTurn = (currentTurn + 1) % this.players.length;
            Arrays.stream(players).forEach((p) -> {
                p.setCurrentTurn(currentTurn);
                p.triggerChange();
            });
        }

        return new GameResult(this.playerPawn.inverse().get(this.gameBoard.getWinner()), history);
    }
}

