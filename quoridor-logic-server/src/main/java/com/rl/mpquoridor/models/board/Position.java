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
    private final int i;
    private final int j;

    public Position(Position p) {
        this.i = p.i;
        this.j = p.j;
    }
}
