package com.rl.mpquoridor.models;

import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.players.Player;
import com.sun.istack.NotNull;
import org.springframework.data.util.Pair;

import java.util.Collections;
import java.util.List;

public class GameResult {
    private final Player winner;
    private final List<Pair<Player, TurnAction>> history;

    public GameResult(@NotNull Player winner, List<Pair<Player, TurnAction>> history) {
        this.winner = winner;
        this.history = history;
    }

    public Player getWinner() {
        return winner;
    }

    public List<Pair<Player, TurnAction>> getHistory() {
        return Collections.unmodifiableList(history);
    }
}
