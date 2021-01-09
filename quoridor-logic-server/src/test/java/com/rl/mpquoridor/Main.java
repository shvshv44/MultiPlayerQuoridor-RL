package com.rl.mpquoridor;

import com.rl.mpquoridor.controllers.GameTCPSocket;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(4000);

        GameTCPSocket gameTCPSocket = new GameTCPSocket(serverSocket.accept());

        System.out.println(gameTCPSocket.read());
        gameTCPSocket.send("aaaaaa");

        gameTCPSocket.close();
        serverSocket.close();


    }
}
