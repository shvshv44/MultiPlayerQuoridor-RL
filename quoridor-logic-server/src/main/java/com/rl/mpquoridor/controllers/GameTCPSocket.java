package com.rl.mpquoridor.controllers;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

public class GameTCPSocket implements Closeable {
    private Socket client;

    public GameTCPSocket(Socket client) {
        this.client = client;
    }

    public void send(String msg) throws IOException {
        byte[] buffer = msg.getBytes();
        client.getOutputStream().write(buffer);
    }

    public String read() throws IOException {
        byte[] buffer = new byte[1024];
        int read = client.getInputStream().read(buffer);
        return new String(buffer, 0, read);
    }

    @Override
    public void close() throws IOException {
        this.client.close();
    }
}
