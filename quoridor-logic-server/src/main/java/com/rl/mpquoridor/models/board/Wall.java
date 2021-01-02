package com.rl.mpquoridor.models.board;

import com.rl.mpquoridor.models.enums.WallDirection;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class Wall {
    private Position position;
    private WallDirection wallDirection;
}
