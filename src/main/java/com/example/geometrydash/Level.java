package com.example.geometrydash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Mapa determinístico, metadados de progresso e portais de uma fase. */
public final class Level {
    private static final double SPEED = 270.0;
    private final int id;
    private final double length;
    private final List<Obstacle> obstacles;
    private final List<GravityPortal> portals;
    private double cameraX;

    public Level() { this(1); }

    public Level(int id) {
        if (id != 1 && id != 2) throw new IllegalArgumentException("Fase não implementada: " + id);
        this.id = id;
        List<Obstacle> map = new ArrayList<>();
        List<GravityPortal> phasePortals = new ArrayList<>();
        if (id == 1) {
            buildPhaseOne(map);
            length = 6500.0;
        } else {
            buildPhaseTwo(map, phasePortals);
            length = 11300.0;
        }
        obstacles = Collections.unmodifiableList(map);
        portals = Collections.unmodifiableList(phasePortals);
    }

    private void buildPhaseOne(List<Obstacle> map) {
        // Mapa preservado sem alterações.
        addSpikes(map, 620, 3, 46, GravityDirection.DOWN);
        map.add(new Block(1390));
        addSpikes(map, 1780, 2, 46, GravityDirection.DOWN);
        map.add(new Block(2280));
        addSpikes(map, 2720, 3, 46, GravityDirection.DOWN);
        map.add(new Block(3350));
        addSpikes(map, 3790, 2, 46, GravityDirection.DOWN);
        map.add(new Block(4310));
        addSpikes(map, 4760, 3, 46, GravityDirection.DOWN);
        map.add(new Block(5450));
        addSpikes(map, 5880, 2, 46, GravityDirection.DOWN);
    }

    private void buildPhaseTwo(List<Obstacle> map, List<GravityPortal> phasePortals) {
        addSpikes(map, 460, 2, 46, GravityDirection.DOWN);

        // Escada: cada degrau fica a cerca de 0,78 s do seguinte, exigindo pulos ritmados.
        int[] stairHeights = {42, 54, 68, 82, 92, 80, 64, 48};
        for (int i = 0; i < stairHeights.length; i++) {
            map.add(new Block(1250 + i * 210, 40, stairHeights[i], GravityDirection.DOWN));
        }
        addSpikes(map, 3130, 3, 44, GravityDirection.DOWN);
        map.add(new Block(3630, 40, 56, GravityDirection.DOWN));
        addSpikes(map, 4080, 2, 46, GravityDirection.DOWN);
        map.add(new Block(4380, 40, 45, GravityDirection.DOWN));

        phasePortals.add(new GravityPortal(4720, GravityDirection.UP));
        // Obstáculos de teto: em gravidade invertida, saltar afasta o jogador deles.
        map.add(new Block(5160, 42, 55, GravityDirection.UP));
        addSpikes(map, 5600, 2, 48, GravityDirection.UP);
        map.add(new Block(6080, 42, 74, GravityDirection.UP));
        addSpikes(map, 6470, 3, 44, GravityDirection.UP);
        phasePortals.add(new GravityPortal(6960, GravityDirection.DOWN));

        addSpikes(map, 7450, 2, 46, GravityDirection.DOWN);
        map.add(new Block(7930, 40, 60, GravityDirection.DOWN));
        map.add(new Block(8420, 40, 56, GravityDirection.DOWN));
        map.add(new Block(8650, 40, 74, GravityDirection.DOWN));
        map.add(new Block(8880, 40, 60, GravityDirection.DOWN));
        addSpikes(map, 9360, 3, 44, GravityDirection.DOWN);
        map.add(new Block(9880, 40, 84, GravityDirection.DOWN));
        addSpikes(map, 10380, 2, 46, GravityDirection.DOWN);
        map.add(new Block(10830, 40, 56, GravityDirection.DOWN));
    }

    private void addSpikes(List<Obstacle> map, double start, int count, int spacing,
                           GravityDirection direction) {
        for (int i = 0; i < count; i++) map.add(new Spike(start + i * spacing, direction));
    }

    public void reset() {
        cameraX = 0;
        for (GravityPortal portal : portals) portal.reset();
    }
    public void update(double deltaSeconds) { cameraX += SPEED * deltaSeconds; }
    public int getId() { return id; }
    public double getCameraX() { return cameraX; }
    public double getSpeed() { return SPEED; }
    public double getLength() { return length; }
    public boolean isComplete() { return cameraX >= length; }
    public List<Obstacle> getObstacles() { return obstacles; }
    public List<GravityPortal> getPortals() { return portals; }
}
