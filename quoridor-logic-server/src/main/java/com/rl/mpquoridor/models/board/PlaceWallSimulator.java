package com.rl.mpquoridor.models.board;

import com.rl.mpquoridor.exceptions.IllegalMovementException;
import com.rl.mpquoridor.paths.AllShortestPaths;

public interface PlaceWallSimulator {

    AllShortestPaths simulatePlaceWall(Pawn pawn, Wall wall) throws IllegalMovementException;

}
