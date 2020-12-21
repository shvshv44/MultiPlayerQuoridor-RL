package com.rl.mpquoridor.models.game;

import com.rl.mpquoridor.models.players.Player;
import com.sun.istack.NotNull;

import java.util.Collections;
import java.util.List;

public class GameResult {
    private final Player winner;
    private final List<HistoryRecord> history;

    public GameResult(@NotNull Player winner, List<HistoryRecord> history) {
        this.winner = winner;
        this.history = history;
    }

    public Player getWinner() {
        return winner;
    }

    public List<HistoryRecord> getHistory() {
        return Collections.unmodifiableList(history);
    }
}
