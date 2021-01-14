package com.rl.mpquoridor.models.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public enum WallDirection {
    @JsonProperty("Right")
    @SerializedName("Right")
    RIGHT,

    @JsonProperty("Down")
    @SerializedName("Down")
    DOWN
}
