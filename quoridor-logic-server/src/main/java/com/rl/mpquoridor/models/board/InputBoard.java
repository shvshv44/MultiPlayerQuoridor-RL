package com.rl.mpquoridor.models.board;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;
import java.util.UUID;

/**
 * A model that allows clients to send board state to Java server. (2 players only)
 */
public class InputBoard {
    private final UUID p1UUID;
    private final UUID p2UUID;
    private final Position p1Pos;
    private final Position p2Pos;
    private final Set<Wall> walls;
    private final int p1Walls;
    private final int p2Walls;
    private final boolean p1Turn;
    private final Set<Position> p1EndLine;
    private final Set<Position> p2EndLine;

    @JsonCreator()
    public InputBoard(@JsonProperty("p1UUID") UUID p1UUID,
                      @JsonProperty("p2UUID") UUID p2UUID,
                      @JsonProperty("p1Pos") Position p1Pos,
                      @JsonProperty("p2Pos") Position p2Pos,
                      @JsonProperty("walls") Set<Wall> walls,
                      @JsonProperty("p1Walls") int p1Walls,
                      @JsonProperty("p2Walls") int p2Walls,
                      @JsonProperty("p1Turn") boolean p1Turn,
                      @JsonProperty("p1EndLine") Set<Position> p1EndLine,
                      @JsonProperty("p2EndLine") Set<Position> p2EndLine) {
        this.p1UUID = p1UUID;
        this.p2UUID = p2UUID;
        this.p1Pos = p1Pos;
        this.p2Pos = p2Pos;
        this.walls = walls;
        this.p1Walls = p1Walls;
        this.p2Walls = p2Walls;
        this.p1Turn = p1Turn;
        this.p1EndLine = p1EndLine;
        this.p2EndLine = p2EndLine;
    }

    public UUID getP1UUID() {
        return p1UUID;
    }

    public UUID getP2UUID() {
        return p2UUID;
    }


    public Position getP1Pos() {
        return p1Pos;
    }

    public Position getP2Pos() {
        return p2Pos;
    }

    public Set<Wall> getWalls() {
        return walls;
    }

    public int getP1Walls() {
        return p1Walls;
    }

    public int getP2Walls() {
        return p2Walls;
    }

    public boolean isP1Turn() {
        return p1Turn;
    }

    public Set<Position> getP1EndLine() {
        return p1EndLine;
    }

    public Set<Position> getP2EndLine() {
        return p2EndLine;
    }
}
