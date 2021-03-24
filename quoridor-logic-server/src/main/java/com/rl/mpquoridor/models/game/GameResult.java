package com.rl.mpquoridor.models.game;

import com.rl.mpquoridor.models.board.Pawn;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class GameResult {
    private Pawn winner;
    private List<HistoryRecord> history;
    private List<Pawn> playOrder;
    private int startingWallCount;

    public Pawn getWinner() {
        return winner;
    }

    public int getStartingWallCount() {
        return startingWallCount;
    }

    public void setStartingWallCount(int startingWallCount) {
        this.startingWallCount = startingWallCount;
    }

    public void setWinner(Pawn winner) {
        this.winner = winner;
    }

    public void setHistory(List<HistoryRecord> history) {
        this.history = new ArrayList<>(history);
    }

    public void setPlayOrder(List<Pawn> playOrder) {
        this.playOrder = new ArrayList<>(playOrder);
    }

    public List<HistoryRecord> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public List<Pawn> getPlayOrder() {
        return Collections.unmodifiableList(playOrder);
    }

}
