package com.rl.mpquoridor.models.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum WebSocketMessageType {

    @JsonProperty("RoomStateRequest")
    ROOM_STATE_REQUEST,

    @JsonProperty("RoomStateResponse")
    ROOM_STATE_RESPONSE,

    @JsonProperty("StartGameMessage")
    START_GAME_EVENT,

    @JsonProperty("NewTurnEvent")
    NEW_TURN_EVENT,

    @JsonProperty("GameOverEvent")
    GAME_OVER_EVENT

}
