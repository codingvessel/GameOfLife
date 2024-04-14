package com.codingvessel.gameoflife;

import com.badlogic.gdx.math.Vector2;

import java.util.Map;

public class Cell {

    private boolean alive;

    public int x;
    public int y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     *     Warning is supressed so the typical rules for conways game of life are clearly visible from the code
     */
    @SuppressWarnings("ConstantConditions")
    public void setHealthStatus(int x, int y, Map<Vector2, Cell> grid) {
        int neighbours_alive = countNeighbours(x, y, grid);
        if (isAlive()) {
            if (neighbours_alive <= 1) {
                setAlive(false);
            } else if (neighbours_alive >= 4) {
                setAlive(false);
            } else if (neighbours_alive == 2 || neighbours_alive == 3) {
                setAlive(true);
            }
        } else {
            if (neighbours_alive == 3) {
                setAlive(true);
            }
        }
    }

    private int countNeighbours(int x, int y, Map<Vector2, Cell> grid) {
        int count = 0;
        if (grid.get(new Vector2(x + 1, y)) != null && grid.get(new Vector2(x + 1, y)).isAlive()) {
            count++;
        }
        if (grid.get(new Vector2(x, y + 1)) != null && grid.get(new Vector2(x, y + 1)).isAlive()) {
            count++;
        }
        if (grid.get(new Vector2(x, y - 1)) != null && grid.get(new Vector2(x, y - 1)).isAlive()) {
            count++;
        }
        if (grid.get(new Vector2(x - 1, y)) != null && grid.get(new Vector2(x - 1, y)).isAlive()) {
            count++;
        }
        if (grid.get(new Vector2(x + 1, y + 1)) != null && grid.get(new Vector2(x + 1, y + 1)).isAlive()) {
            count++;
        }
        if (grid.get(new Vector2(x + 1, y - 1)) != null && grid.get(new Vector2(x + 1, y - 1)).isAlive()) {
            count++;
        }
        if (grid.get(new Vector2(x - 1, y + 1)) != null && grid.get(new Vector2(x - 1, y + 1)).isAlive()) {
            count++;
        }
        if (grid.get(new Vector2(x - 1, y - 1)) != null && grid.get(new Vector2(x - 1, y - 1)).isAlive()) {
            count++;
        }
        return count;
    }
}
