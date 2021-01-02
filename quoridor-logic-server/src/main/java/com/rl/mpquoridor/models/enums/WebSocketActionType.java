package com.rl.mpquoridor.models.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum WebSocketActionType {

    @JsonProperty("MovePawn")
    MOVE_PAWN,

    @JsonProperty("PlaceWall")
    PLACE_WALL

}
