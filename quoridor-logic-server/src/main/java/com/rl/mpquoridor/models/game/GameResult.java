package com.rl.mpquoridor.models.game;

import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.Position;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
public class GameResult {
    private String gameId;
    private Pawn winner;
    private List<HistoryRecord> history;
    private List<Pawn> playOrder;
    private int startingWallCount;
    private Map<Pawn, Position> startingPosition;
}
