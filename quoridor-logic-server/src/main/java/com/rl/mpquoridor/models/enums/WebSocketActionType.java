package com.rl.mpquoridor.models.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public enum WebSocketActionType {

    @JsonProperty("MovePawn")
    @SerializedName("MovePawn")
    MOVE_PAWN,

    @JsonProperty("PlaceWall")
    @SerializedName("PlaceWall")
    PLACE_WALL

}
