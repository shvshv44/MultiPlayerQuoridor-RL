package com.rl.mpquoridor.models.board;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Pawn {
    private final UUID uuid;

    public Pawn() {
        this(UUID.randomUUID());
    }

    public Pawn(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}
