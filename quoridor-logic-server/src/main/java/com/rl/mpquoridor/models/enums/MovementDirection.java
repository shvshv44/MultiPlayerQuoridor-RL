package com.rl.mpquoridor.models.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public enum MovementDirection {
    @JsonProperty("Up")
    @SerializedName("Up")
    UP,

    @JsonProperty("Down")
    @SerializedName("Down")
    DOWN,

    @JsonProperty("Left")
    @SerializedName("Left")
    LEFT,

    @JsonProperty("Right")
    @SerializedName("Right")
    RIGHT
}
