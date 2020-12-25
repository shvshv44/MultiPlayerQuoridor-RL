package com.rl.mpquoridor.models.game;

import lombok.Data;

import java.util.List;

@Data
public class GameRoomState {

    private String id;
    private List<String> players;
    private GameManager manager;

}
