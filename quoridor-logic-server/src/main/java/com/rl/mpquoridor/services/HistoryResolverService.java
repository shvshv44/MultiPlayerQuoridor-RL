package com.rl.mpquoridor.services;

import com.rl.mpquoridor.database.MongoDB;
import com.rl.mpquoridor.models.actions.MovePawnAction;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.game.GameResult;
import com.rl.mpquoridor.models.game.HistoryRecord;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class HistoryResolverService {

    public List<Document> fetchAllHistoryGameIds() {
        List<Document> lst = new LinkedList<>();
        for (Document d: MongoDB.getInstance().selectAll()) {
            d.remove("_id");
            lst.add(d);
        }
        return lst;

    }

    public GameResult getResultByGameId(String gameId) {
        // TODO: use mongo JPA Repository to fetch it from DB

        // Fake Data till DB implemented
        Pawn fakePawn = new Pawn();
        TurnAction fakeTurn = new MovePawnAction();
        HistoryRecord fakeHistoryRecord = new HistoryRecord(fakePawn, fakeTurn);
        GameResult fakeGameResult = new GameResult();
        fakeGameResult.setWinner(fakePawn);
        fakeGameResult.setHistory(Collections.singletonList(fakeHistoryRecord));
        return fakeGameResult;
    }

}
