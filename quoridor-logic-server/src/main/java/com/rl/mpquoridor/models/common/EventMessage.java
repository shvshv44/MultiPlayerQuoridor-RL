package com.rl.mpquoridor.models.common;

import com.rl.mpquoridor.models.enums.MessageType;
import lombok.Data;

@Data
public class EventMessage {
    private MessageType type;
}
