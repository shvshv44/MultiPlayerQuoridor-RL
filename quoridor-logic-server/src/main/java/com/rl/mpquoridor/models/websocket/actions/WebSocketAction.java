package com.rl.mpquoridor.models.websocket.actions;

import com.rl.mpquoridor.models.board.Position;
import com.rl.mpquoridor.models.board.Wall;
import com.rl.mpquoridor.models.enums.WebSocketActionType;
import lombok.Data;

@Data
public class WebSocketAction {

    private WebSocketActionType type;
    private Wall wall;
    private Position pawnPosition;
    private int numOfWalls;

}
