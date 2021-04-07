package com.rl.mpquoridor.models.game;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.rl.mpquoridor.exceptions.IllegalMovementException;
import com.rl.mpquoridor.models.actions.MovePawnAction;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.GameBoard;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.Position;
import com.rl.mpquoridor.models.events.EndTurnEvent;
import com.rl.mpquoridor.models.events.GameEvent;
import com.rl.mpquoridor.models.events.NewTurnEvent;
import com.rl.mpquoridor.models.events.StartGameEvent;
import com.rl.mpquoridor.models.players.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class GameManager {
    private final static Logger logger = LoggerFactory.getLogger(GameManager.class);
    private final GameBoard gameBoard;
    private final Queue<Player> players = new LinkedList<>();
    private BiMap<Player, Pawn> playerPawn;
    private final int numberOfWallsPerPlayer;
    private final String gameId;

    public BiMap<Player, Pawn> getPlayerPawn() {
        return playerPawn;
    }

    public GameManager(String gameId, Collection<Player> players, int numberOfWallsPerPlayer) {
        this.players.addAll(players);
        this.gameId = gameId;
        this.numberOfWallsPerPlayer = numberOfWallsPerPlayer;
        this.gameBoard = new GameBoard(this.players.size(), numberOfWallsPerPlayer);
        initPlayerPawn();
    }

    public Queue<Player> getPlayers() {
        return players;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    private void notifyStartGameToPlayers() {
        for (Player p : this.players) {
            p.setBoard(this.gameBoard.getReadOnlyPhysicalBoard());
            p.setPlayOrder(this.gameBoard.getPlayOrder());
            p.setMyPawn(playerPawn.get(p));
        }
    }

    public void initPlayerPawn() {
        this.playerPawn = HashBiMap.create(this.players.size());
        List<Pawn> playOrder = this.gameBoard.getPlayOrder();

        Iterator<Player> itPlayer = this.players.iterator();
        Iterator<Pawn> itPawn = playOrder.iterator();

        while (itPawn.hasNext() && itPlayer.hasNext()) {
            this.playerPawn.put(itPlayer.next(), itPawn.next());
        }

    }

    public GameResult run() {
        List<HistoryRecord> history = new LinkedList<>();
        GameResult gameResult = new GameResult();
        gameResult.setGameId(this.gameId);
        gameResult.setStartingWallCount(numberOfWallsPerPlayer);
        gameResult.setPlayOrder(this.gameBoard.getPlayOrder());
        gameResult.setStartingPosition(this.gameBoard.getReadOnlyPhysicalBoard().getAllPawnPosition());
        boolean isGameEnded = (this.gameBoard.getWinner() != null);
        notifyStartGameToPlayers();
        trigger(new StartGameEvent(playerPawn.inverse(), this.numberOfWallsPerPlayer));
        while (!isGameEnded) {
            Player currentPlayer = this.players.peek();
            Pawn currentPawn = playerPawn.get(currentPlayer);
            trigger(new NewTurnEvent(currentPawn, this.gameBoard.getCurrentPlayerMoves(currentPawn), this.gameBoard.getAvailableWalls(currentPawn)));

            TurnAction action = currentPlayer.play();
            try {
                this.gameBoard.executeAction(currentPawn, action);
            } catch (IllegalMovementException e) {
                currentPlayer.illegalMovePlayed(e.getReason());
                logger.info("Illegal action " + e.getReason().getMessage());
                continue;
            }
            isGameEnded = (this.gameBoard.getWinner() != null);
            List<Position> nextPlayerMoves = this.gameBoard.getCurrentPlayerMoves(this.playerPawn.get(currentPlayer));

            HistoryRecord record = new HistoryRecord(currentPawn, action);
            if(action instanceof MovePawnAction) {
                record.addDetail("position", this.gameBoard.getReadOnlyPhysicalBoard().getPawnPosition(currentPawn));
            }
            history.add(record);
            this.players.add(this.players.poll()); // Move the current player to the end of the queue
            trigger(new EndTurnEvent(currentPawn, action));
        }

        trigger(new GameOverEvent(this.gameBoard.getWinner()));
        gameResult.setHistory(history);
        gameResult.setWinner(this.gameBoard.getWinner());
        return gameResult;
    }

    private void trigger(GameEvent event) {
        this.players.forEach(p -> p.trigger(event));
    }
}

