package com.rl.mpquoridor.models.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum WallDirection {
    @JsonProperty("Right")
    RIGHT,

    @JsonProperty("Down")
    DOWN
}
