package com.rl.mpquoridor.models.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public enum MessageType {

    @JsonProperty("RoomStateRequest")
    @SerializedName("RoomStateRequest")
    ROOM_STATE_REQUEST,

    @JsonProperty("RoomStateResponse")
    @SerializedName("RoomStateResponse")
    ROOM_STATE_RESPONSE,

    @JsonProperty("StartGameMessage")
    @SerializedName("StartGameMessage")
    START_GAME_EVENT,

    @JsonProperty("NewTurnEvent")
    @SerializedName("NewTurnEvent")
    NEW_TURN_EVENT,

    @JsonProperty("GameOverEvent")
    @SerializedName("GameOverEvent")
    GAME_OVER_EVENT,

    @JsonProperty("EndTurnEvent")
    @SerializedName("EndTurnEvent")
    END_TURN_EVENT

}
