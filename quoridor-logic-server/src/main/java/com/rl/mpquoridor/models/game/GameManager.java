package com.rl.mpquoridor.models.game;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.rl.mpquoridor.exceptions.IllegalMovementException;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.GameBoard;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.Position;
import com.rl.mpquoridor.models.events.GameEvent;
import com.rl.mpquoridor.models.events.NewTurnEvent;
import com.rl.mpquoridor.models.events.StartGameEvent;
import com.rl.mpquoridor.models.events.TurnActionEvent;
import com.rl.mpquoridor.models.players.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class GameManager {
    private final static Logger logger = LoggerFactory.getLogger(GameManager.class);
    private final GameBoard gameBoard;
    private final Queue<Player> players = new LinkedList<>();
    private BiMap<Player, Pawn> playerPawn;

    public GameManager(Collection<Player> players, int numberOfWallsPerPlayer) {
        this.players.addAll(players);
        this.gameBoard = new GameBoard(this.players.size(), numberOfWallsPerPlayer);
        initPlayerPawn();
    }

    private void notifyStartGameToPlayers() {
        for(Player p: this.players) {
            p.setBoard(this.gameBoard.getReadOnlyPhysicalBoard());
            p.setPlayOrder(this.gameBoard.getPlayOrder());
            p.setMyPawn(playerPawn.get(p));
        }
    }

    public void initPlayerPawn() {
        this.playerPawn =  HashBiMap.create(this.players.size());
        List<Pawn> playOrder = this.gameBoard.getPlayOrder();

        Iterator<Player> itPlayer = this.players.iterator();
        Iterator<Pawn> itPawn = playOrder.iterator();

        while(itPawn.hasNext() && itPlayer.hasNext()) {
            this.playerPawn.put(itPlayer.next(), itPawn.next());
        }

    }
    public GameResult run() {
        List<HistoryRecord> history = new ArrayList<>();
        boolean isGameEnded = (this.gameBoard.getWinner() != null);
        notifyStartGameToPlayers();
        trigger(new StartGameEvent());

        while(!isGameEnded) {
            Player currentPlayer = this.players.peek();
            Pawn currentPawn = playerPawn.get(currentPlayer);
            trigger(new NewTurnEvent(currentPawn));
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

            history.add(new HistoryRecord(currentPawn, action));
            this.players.add(this.players.poll()); // Move the current player to the end of the queue
            trigger(new TurnActionEvent(this.playerPawn.get(currentPlayer), action,
                    this.players.peek().getPlayerName(), isGameEnded, nextPlayerMoves));
        }

        return new GameResult(this.playerPawn.inverse().get(this.gameBoard.getWinner()), history);
    }

    private void trigger(GameEvent event) {
        this.players.forEach(p -> p.trigger(event));
    }
}

