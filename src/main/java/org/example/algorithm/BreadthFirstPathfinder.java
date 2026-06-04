package org.example.algorithm;

import java.util.*;

public class BreadthFirstPathfinder {
    public List<Coordinate> findPath(boolean[][] mazeMap) {
        if (mazeMap == null || mazeMap.length == 0) return Collections.emptyList();

        int height = mazeMap.length;
        int width = mazeMap[0].length;

        Queue<Coordinate> queue = new LinkedList<>();
        Map<Coordinate, Coordinate> parentMap = new HashMap<>();
        boolean[][] visited = new boolean[height][width];

        Coordinate start = new Coordinate(0, 0);
        Coordinate end = new Coordinate(width - 1, height - 1);

        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        boolean found = false;

        queue.add(start);
        visited[start.y()][start.x()] = true;

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();

            if (current.equals(end)) {
                found = true;
                break;
            }

            for (int[] dir : directions) {
                int nx = current.x() + dir[0];
                int ny = current.y() + dir[1];

                if (nx >= 0 && nx < width && ny >= 0 && ny < height && mazeMap[ny][nx] && !visited[ny][nx]) {
                    Coordinate neighbor = new Coordinate(nx, ny);
                    visited[ny][nx] = true;
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        if (!found) return Collections.emptyList();

        List<Coordinate> finalPath = new ArrayList<>();
        Coordinate curr = end;
        while (curr != null) {
            finalPath.add(curr);
            curr = parentMap.get(curr);
        }

        Collections.reverse(finalPath);
        return finalPath;
    }
}
