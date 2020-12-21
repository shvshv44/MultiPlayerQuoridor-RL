package com.rl.mpquoridor.models.game;
import com.google.common.collect.HashBiMap;
import com.rl.mpquoridor.exceptions.IllegalMovementException;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.GameBoard;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.events.GameEvent;
import com.rl.mpquoridor.models.events.NewTurnEvent;
import com.rl.mpquoridor.models.events.TurnActionEvent;
import com.rl.mpquoridor.models.players.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.BiMap;
import java.util.*;


public class GameManager {
    private final static Logger logger = LoggerFactory.getLogger(GameManager.class);
    private final GameBoard gameBoard;
    private final Queue<Player> players = new LinkedList<>();
    private BiMap<Player, Pawn> playerPawn;

    public GameManager(List<Player> players, int numberOfWallsPerPlayer) {
        this.players.addAll(players);
        this.gameBoard = new GameBoard(this.players.size(), numberOfWallsPerPlayer);
        initPlayerPawn();

        for(Player p: this.players) {
            p.setMyPawn(playerPawn.get(p));
            p.setPlayOrder(this.gameBoard.getPlayOrder());
        }

    }

    public void initPlayerPawn() {
        this.playerPawn =  HashBiMap.create(this.players.size());
        List<Pawn> playOrder = this .gameBoard.getPlayOrder();

        Iterator<Player> itPlayer = this.players.iterator();
        Iterator<Pawn> itPawn = playOrder.iterator();

        while(itPawn.hasNext() && itPlayer.hasNext()) {
            this.playerPawn.put(itPlayer.next(), itPawn.next());
        }

    }
    public GameResult run() {
        List<HistoryRecord> history = new ArrayList<>();
        while(this.gameBoard.getWinner() == null) {
            Player currentPlayer = this.players.peek();
            Pawn currentPawn = playerPawn.get(currentPlayer);
            trigger(new NewTurnEvent(currentPawn));
            TurnAction action = currentPlayer.play();
            try {
                this.gameBoard.executeAction(currentPawn, action);
            } catch (IllegalMovementException e) {
                currentPlayer.illegalMovePlayed(e.getMessage());
                logger.info("Illegal action " + e.getMessage());
                continue;
            }
            history.add(new HistoryRecord(currentPawn, action));
            trigger(new TurnActionEvent(this.playerPawn.get(currentPlayer), action));
            this.players.add(this.players.poll()); // Move the current player to the end of the queue
        }

        return new GameResult(this.playerPawn.inverse().get(this.gameBoard.getWinner()), history);
    }

    private void trigger(GameEvent event) {
        this.players.forEach(p -> p.trigger(event));
    }
}

