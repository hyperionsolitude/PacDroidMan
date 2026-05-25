package com.example.pacdroidman.model

class GameMap {
    val width = 21
    val height = 21
    val tiles = Array(height) { Array(width) { TileType.EMPTY } }

    init {
        setupMaze()
    }

    fun setupMaze() {
        val layout = arrayOf(
            "#####################",
            "#...................#",
            "#.###.#.#####.###.#.#",
            "#...#...#...#...#...#",
            "#.###.#.#.#.#.#.#.#.#",
            "#...#...#...#...#...#",
            "###.###.#.#.#.###.###",
            "###...#...#...#...###",
            "###.###.#.#.#.###.###",
            "#...#...#...#...#...#",
            "###...#...#...#...###",
            "#...#...#...#...#...#",
            "#.###.#.#.#.#.#.#.#.#",
            "#...#...#...#...#...#",
            "###.###.#.#.#.###.###",
            "###...#...#...#...###",
            "###.###.#.#.#.###.###",
            "#...#...#...#...#...#",
            "#.###.#.#####.###.#.#",
            "#...................#",
            "#####################"
        )

        for (y in layout.indices) {
            for (x in layout[y].indices) {
                val char = layout[y][x]
                tiles[y][x] = when (char) {
                    '#' -> TileType.WALL
                    '.' -> TileType.PELLET
                    'O' -> TileType.POWER_PELLET
                    else -> TileType.EMPTY
                }
            }
        }

        // Randomize Power Pellets
        val pelletTiles = mutableListOf<Pair<Int, Int>>()
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (tiles[y][x] == TileType.PELLET) {
                    pelletTiles.add(Pair(x, y))
                }
            }
        }

        val random = java.util.Random()
        repeat(4) {
            if (pelletTiles.isNotEmpty()) {
                val idx = random.nextInt(pelletTiles.size)
                val (rx, ry) = pelletTiles.removeAt(idx)
                tiles[ry][rx] = TileType.POWER_PELLET
            }
        }
    }

    fun getTile(x: Int, y: Int): TileType {
        if (x < 0 || x >= width || y < 0 || y >= height) return TileType.WALL
        return tiles[y][x]
    }
}
