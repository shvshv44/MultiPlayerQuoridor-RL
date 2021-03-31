package com.rl.mpquoridor.models.game;

import com.rl.mpquoridor.models.board.Pawn;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class GameResult {
    private String gameId;
    private Pawn winner;
    private List<HistoryRecord> history;
    private List<Pawn> playOrder;
    private int startingWallCount;
}
