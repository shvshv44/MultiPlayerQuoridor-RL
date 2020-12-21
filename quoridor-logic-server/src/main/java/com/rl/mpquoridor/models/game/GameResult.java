package com.rl.mpquoridor.models.game;

import com.rl.mpquoridor.models.players.Player;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class GameResult {
    @Getter
    private final Player winner;
    private final List<HistoryRecord> history;

    public List<HistoryRecord> getHistory() {
        return Collections.unmodifiableList(history);
    }
}
