package com.rl.mpquoridor.exceptions;

public class IllegalMovementException extends  RuntimeException {
    public IllegalMovementException(String s) {
        super(s);
    }
}
