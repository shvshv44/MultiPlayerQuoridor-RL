package com.rl.mpquoridor.models.common;

import com.rl.mpquoridor.models.enums.WebSocketMessageType;
import lombok.Data;

@Data
public class WebSocketMessage {
    private WebSocketMessageType type;
    private String gameID;
}
