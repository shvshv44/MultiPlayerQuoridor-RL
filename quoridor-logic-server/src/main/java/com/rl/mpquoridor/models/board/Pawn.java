package com.rl.mpquoridor.models.board;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
public class Pawn {
    private final UUID uuid = UUID.randomUUID();

    @Override
    public String toString() {
        return uuid.toString();
    }
}
