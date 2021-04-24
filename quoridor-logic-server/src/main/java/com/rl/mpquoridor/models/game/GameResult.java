package com.rl.mpquoridor.models.game;

import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.Position;
import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor
public class GameResult {
    private String gameId;
    private Pawn winner;
    private List<HistoryRecord> history;
    private List<Pawn> playOrder;
    private int startingWallCount;
    private Map<Pawn, Position> startingPosition;
    private Map<Pawn, Set<Position>> pawnEndLine;

    public Map<Pawn, Set<Position>> getPawnEndLine() {
        return Collections.unmodifiableMap(pawnEndLine);
    }

    public void setPawnEndLine(final Map<Pawn, Set<Position>> pawnEndLine) {
        this.pawnEndLine = new HashMap<>();
        pawnEndLine.forEach((p, e) -> {
            this.pawnEndLine.put(p, new HashSet<>(e));
        });
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setWinner(Pawn winner) {
        this.winner = winner;
    }

    public void setHistory(List<HistoryRecord> history) {
        this.history = new LinkedList<>(history);
    }

    public void setPlayOrder(List<Pawn> playOrder) {
        this.playOrder = new LinkedList<>(playOrder);
    }

    public void setStartingWallCount(int startingWallCount) {
        this.startingWallCount = startingWallCount;
    }

    public void setStartingPosition(Map<Pawn, Position> startingPosition) {
        this.startingPosition = new HashMap<>(startingPosition);
    }

    public String getGameId() {
        return gameId;
    }

    public Pawn getWinner() {
        return winner;
    }

    public List<HistoryRecord> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public List<Pawn> getPlayOrder() {
        return Collections.unmodifiableList(playOrder);
    }

    public int getStartingWallCount() {
        return startingWallCount;
    }

    public Map<Pawn, Position> getStartingPosition() {
        return Collections.unmodifiableMap(startingPosition);
    }
}
