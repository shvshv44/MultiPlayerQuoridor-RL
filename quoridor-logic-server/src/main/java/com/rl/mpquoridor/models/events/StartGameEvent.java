package com.rl.mpquoridor.models.events;

import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.players.Player;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StartGameEvent implements GameEvent {

    private Map<Pawn, String> pawnPerPlayerName;

    public StartGameEvent(Map<Pawn, Player> pawnPlayer) {
        this.pawnPerPlayerName = new HashMap<>();
        for (Pawn pawn : pawnPlayer.keySet()) {
            this.pawnPerPlayerName.put(pawn, pawnPlayer.get(pawn).getPlayerName());
        }
    }

    public Map<Pawn, String> getPawnPerPlayerName() {
        return Collections.unmodifiableMap(pawnPerPlayerName);
    }
}
