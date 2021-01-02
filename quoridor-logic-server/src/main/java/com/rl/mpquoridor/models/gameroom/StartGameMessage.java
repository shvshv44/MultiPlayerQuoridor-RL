package com.rl.mpquoridor.models.gameroom;

import com.rl.mpquoridor.models.board.Position;
import com.rl.mpquoridor.models.common.WebSocketMessage;
import lombok.Data;

import java.util.List;

@Data
public class StartGameMessage extends WebSocketMessage {

    private List<Position> positions;

}
