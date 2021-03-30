package com.rl.mpquoridor.services;

import com.mongodb.client.FindIterable;
import com.rl.mpquoridor.database.MongoDB;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class HistoryResolverService {

    public List<Document> fetchHistory() {
        return getByMongoDBSupplier(MongoDB.getInstance()::selectAll);

    }

    public List<Document> fetchHistoryGameIds() {
        return getByMongoDBSupplier(MongoDB.getInstance()::selectAllId);
    }

    public Document getById(String id) {
        return MongoDB.getInstance().selectByGameId(id);
    }

    private List<Document> getByMongoDBSupplier(Supplier<FindIterable<Document>> dbSupplier) {
        List<Document> lst = new LinkedList<>();
        for(Document d: dbSupplier.get()) {
            d.remove("_id");
            lst.add(d);
        }
        return lst;
    }

}
