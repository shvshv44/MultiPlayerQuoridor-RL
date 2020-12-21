package com.rl.mpquoridor.models.board;

import com.rl.mpquoridor.models.enums.WallDirection;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Wall {
    private final Position position;
    private final WallDirection wallDirection;

}
