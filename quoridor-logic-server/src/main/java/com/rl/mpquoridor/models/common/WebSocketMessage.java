package com.rl.mpquoridor.models.common;

import lombok.Data;

@Data
public class WebSocketMessage {
    private String type;
    private String gameID;
}
