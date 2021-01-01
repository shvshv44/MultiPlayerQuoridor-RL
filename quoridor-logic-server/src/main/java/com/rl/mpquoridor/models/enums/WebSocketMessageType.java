package com.rl.mpquoridor.models.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum WebSocketMessageType {

    @JsonProperty("RoomStateRequest")
    ROOM_STATE_REQUEST,

    @JsonProperty("RoomStateResponse")
    ROOM_STATE_RESPONSE,

    @JsonProperty("StartGameEvent")
    START_GAME_EVENT

}
