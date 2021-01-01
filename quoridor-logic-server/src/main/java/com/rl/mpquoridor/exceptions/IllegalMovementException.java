package com.rl.mpquoridor.exceptions;

import lombok.Getter;

public class IllegalMovementException extends RuntimeException {
    @Getter
    private final Reason reason;

    public IllegalMovementException(Reason reason) {
        super(reason.getMessage());
        this.reason = reason;
    }

    public enum Reason {
        MOVING_IN_DIRECTION_IS_NOT_ALLOWED("Unable to move in that direction", 1),
        WALL_IS_OUTSIDE_THE_BOARD_BOUNDS("Unable to place wall out side the board bounties", 2),
        WALL_COLLIDES_WITH_OTHER_WALL("Unable to place the wall because it collides with another wall", 3),
        NO_WALLS_LEFT("Unable to place the wall because there are no more walls", 4),
        NO_PATH_AVAILABLE("Unable to place the wall because not all players have available path to their end line", 5);

        @Getter
        private final String message;
        @Getter
        private final int code;

        Reason(String message, int code){
            this.message = message;
            this.code = code;
        }
    }
}
