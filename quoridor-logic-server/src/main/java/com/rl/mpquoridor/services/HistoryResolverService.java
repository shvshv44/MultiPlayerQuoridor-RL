package com.rl.mpquoridor.services;

import com.rl.mpquoridor.models.actions.MovePawnAction;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.enums.MovementDirection;
import com.rl.mpquoridor.models.game.GameResult;
import com.rl.mpquoridor.models.game.HistoryRecord;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class HistoryResolverService {

    public List<String> fetchAllHistoryGameIds() {
        // TODO: use mongo JPA Repository to fetch it from DB

        // Fake Data till DB implemented
        ArrayList<String> fakeIds = new ArrayList<>();
        fakeIds.add("game1");
        fakeIds.add("game2");
        fakeIds.add("game3");
        return fakeIds;
    }

    public GameResult getResultByGameId(String gameId) {
        // TODO: use mongo JPA Repository to fetch it from DB

        // Fake Data till DB implemented
        Pawn fakePawn = new Pawn();
        TurnAction fakeTurn = new MovePawnAction();
        HistoryRecord fakeHistoryRecord = new HistoryRecord(fakePawn, fakeTurn);
        GameResult fakeGameResult = new GameResult(null, Collections.singletonList(fakeHistoryRecord));
        return fakeGameResult;
    }

}
