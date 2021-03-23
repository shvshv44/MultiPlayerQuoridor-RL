package com.rl.mpquoridor.models.board;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Pawn {
    private final UUID uuid = UUID.randomUUID();
}
