package com.rl.mpquoridor.models.game;

import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
public class HistoryRecord {
    private final Pawn pawn;
    private final TurnAction action;
    private final Map<String, Object> details = new HashMap<>();

    public HistoryRecord(Pawn pawn, TurnAction action) {
        this.pawn = pawn;
        this.action = action;
    }

    public Map<String, Object> getDetails() {
        return Collections.unmodifiableMap(details);
    }

    public void addDetail(String key, Object value) {
        this.details.put(key, value);
    }
}
