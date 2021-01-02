package com.rl.mpquoridor.models.gameroom;

import com.rl.mpquoridor.models.board.Position;
import lombok.Data;

@Data
public class PlayerPosition {

    private String name;
    private Position position;

}
