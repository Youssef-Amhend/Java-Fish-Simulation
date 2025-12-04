package com.dtp5.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Spatial partitioning grid for efficient neighbor queries.
 * Reduces fish neighbor checks from O(n²) to O(n).
 */
public class SpatialGrid {
    private final int cellSize;
    private final int gridWidth;
    private final int gridHeight;
    private final List<Poisson>[][] grid;

    @SuppressWarnings("unchecked")
    public SpatialGrid(double width, double height, int cellSize) {
        this.cellSize = cellSize;
        this.gridWidth = (int) Math.ceil(width / cellSize) + 1;
        this.gridHeight = (int) Math.ceil(height / cellSize) + 1;

        grid = (List<Poisson>[][]) new List[gridWidth][gridHeight];
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                grid[x][y] = new ArrayList<>();
            }
        }
    }

    /**
     * Clears all cells in the grid.
     */
    public void clear() {
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                grid[x][y].clear();
            }
        }
    }

    /**
     * Adds a fish to the appropriate grid cell.
     */
    public void addFish(Poisson fish) {
        int cellX = (int) (fish.posX / cellSize);
        int cellY = (int) (fish.posY / cellSize);

        if (cellX >= 0 && cellX < gridWidth && cellY >= 0 && cellY < gridHeight) {
            grid[cellX][cellY].add(fish);
        }
    }

    /**
     * Gets all fish in neighboring cells (including the fish's own cell).
     */
    public List<Poisson> getNearbyFish(Poisson fish) {
        List<Poisson> nearby = new ArrayList<>();

        int cellX = (int) (fish.posX / cellSize);
        int cellY = (int) (fish.posY / cellSize);

        // Check 3x3 grid of cells around the fish
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int checkX = cellX + dx;
                int checkY = cellY + dy;

                if (checkX >= 0 && checkX < gridWidth &&
                        checkY >= 0 && checkY < gridHeight) {
                    nearby.addAll(grid[checkX][checkY]);
                }
            }
        }

        return nearby;
    }

    /**
     * Gets all fish in the grid.
     */
    public List<Poisson> getAllFish() {
        List<Poisson> allFish = new ArrayList<>();
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                allFish.addAll(grid[x][y]);
            }
        }
        return allFish;
    }
}
