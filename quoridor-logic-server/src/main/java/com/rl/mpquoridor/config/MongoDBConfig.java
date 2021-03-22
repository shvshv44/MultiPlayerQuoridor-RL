package com.rl.mpquoridor.config;

public class MongoDBConfig {
    public static final String HOSTNAME = "cluster0-shard-00-00.yp5ll.mongodb.net:27017,cluster0-shard-00-01.yp5ll.mongodb.net:27017,cluster0-shard-00-02.yp5ll.mongodb.net:27017";
    public static final String REPLICA_SET_NAME = "atlas-vzvdu7-shard-0";
    public static final String USERNAME = "quoridor-logic-server";
    public static final String PASSWORD = "uaeAdadS1FYfwfNI";
    public static final String AUTHENTICATION_DATABASE = "admin";
    public static final String DB = "games";
    public static final String COLLECTION="games";
    public static final boolean SSL=true;

    private MongoDBConfig() {}
}
