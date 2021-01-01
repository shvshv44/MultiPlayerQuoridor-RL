package com.rl.mpquoridor;

import com.rl.mpquoridor.models.game.GameManager;
import com.rl.mpquoridor.models.players.CMDPlayer;
import com.rl.mpquoridor.models.players.Player;
import com.rl.mpquoridor.models.players.RandomPlayer;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Player p1 = new CMDPlayer();
        Player p2 = new RandomPlayer();

        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        GameManager gameManager = new GameManager(players, 5);

        gameManager.run();

    }
}
