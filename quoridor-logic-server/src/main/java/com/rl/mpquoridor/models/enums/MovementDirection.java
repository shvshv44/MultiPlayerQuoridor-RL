package com.rl.mpquoridor.models.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MovementDirection {
    @JsonProperty("Up")
    UP,

    @JsonProperty("Down")
    DOWN,

    @JsonProperty("Left")
    LEFT,

    @JsonProperty("Right")
    RIGHT
}
