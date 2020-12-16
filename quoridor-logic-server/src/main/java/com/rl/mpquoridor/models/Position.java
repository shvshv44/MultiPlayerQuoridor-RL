package com.rl.mpquoridor.models;

import java.util.Objects;

public class Position {
    private int i;
    private int j;

    public Position(Position p) {
        this.i = p.i;
        this.j = p.j;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public Position(int i, int j) {
        this.i = i;
        this.j = j;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return i == position.i &&
                j == position.j;
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, j);
    }
}
