package com.rl.mpquoridor.models.board;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class Position {
    private int y;
    private int x;

    public Position(Position p) {
        this.y = p.y;
        this.x = p.x;
    }
}
