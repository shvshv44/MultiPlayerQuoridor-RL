package com.rl.mpquoridor.models.board;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Position {
    private final int x;
    private final int y;

    public Position(Position p) {
        this.x = p.x;
        this.y = p.y;
    }
}
