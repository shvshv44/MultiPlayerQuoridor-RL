package com.rl.mpquoridor.models.gameroom;

import com.rl.mpquoridor.models.board.Position;
import lombok.Data;

import java.util.Set;

@Data
public class PlayerPosition {

    private String name;
    private Position position;
    private Set<Position> endLine;

}
