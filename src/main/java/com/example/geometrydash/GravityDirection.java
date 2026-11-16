package com.example.geometrydash;

/** Direção física da gravidade e superfície em que o jogador corre. */
public enum GravityDirection {
    DOWN(1), UP(-1);

    private final int sign;
    GravityDirection(int sign) { this.sign = sign; }
    public int getSign() { return sign; }
    public GravityDirection opposite() { return this == DOWN ? UP : DOWN; }
}
