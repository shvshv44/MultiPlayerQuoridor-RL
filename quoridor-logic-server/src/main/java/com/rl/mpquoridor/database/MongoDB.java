package com.rl.mpquoridor.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.rl.mpquoridor.models.game.GameResult;
import org.bson.Document;

import java.util.Map;

import static com.rl.mpquoridor.config.MongoDBConfig.*;
public class MongoDB implements AutoCloseable{
    private static MongoDB instance;
    private final MongoClient mongo;
    private final ObjectMapper mapper = new ObjectMapper();

    public static MongoDB getInstance() {
        if(instance == null) {
            instance = new MongoDB();
        }
        return instance;
    }

    private MongoDB() {
        MongoClientURI uri = new MongoClientURI(String.format("mongodb://%s:%s@%s/%s?w=majority&replicaSet=%s&ssl=%s", USERNAME, PASSWORD, HOSTNAME, AUTHENTICATION_DATABASE, REPLICA_SET_NAME, SSL));
        this.mongo = new MongoClient(uri);
    }

    @Override
    public void close() {
        this.mongo.close();
    }

    public void save(GameResult result) {
        this.mongo.getDatabase(DB).getCollection(COLLECTION).insertOne(convert(result));
    }

    public FindIterable<Document> selectAll() {
        return this.mongo.getDatabase(DB).getCollection(COLLECTION).find();
    }

    private Document convert(GameResult obj) {
        Map<String, Object> map = mapper.convertValue(obj, new TypeReference<Map<String, Object>>() {});
        return new Document(map);
    }
}
