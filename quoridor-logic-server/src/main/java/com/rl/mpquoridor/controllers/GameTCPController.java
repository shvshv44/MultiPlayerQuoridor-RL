package com.rl.mpquoridor.controllers;

import com.rl.mpquoridor.models.players.TCPPlayer;
import com.rl.mpquoridor.services.GameRoomsManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

@Controller
public class GameTCPController {
    private final GameRoomsManagerService gameRoomManager;
    private final ServerSocket server;

    @Autowired
    public GameTCPController(GameRoomsManagerService gameRoomManager) {
        this.gameRoomManager = gameRoomManager;

        try {
            this.server = new ServerSocket(14000);
            new Thread(this::serverThread).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void serverThread() {
        try {
            while(true) {
                handleClient(server.accept());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket client) {
        try {
            byte[] buffer = new byte[1024];
            int read = client.getInputStream().read(buffer);
            String gameId = new String(buffer, 0, read);
            TCPPlayer player = new TCPPlayer("Player: " + UUID.randomUUID().toString(), new GameTCPSocket(client));
            gameRoomManager.joinGame(gameId, player);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
