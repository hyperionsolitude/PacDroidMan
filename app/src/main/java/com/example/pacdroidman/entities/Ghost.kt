package com.example.pacdroidman.entities

import com.example.pacdroidman.model.GameMap
import com.example.pacdroidman.utils.Vector2D

enum class GhostMode {
    CHASE,
    SCATTER,
    FRIGHTENED
}

open class Ghost(x: Int, y: Int, var color: Int) : Entity(x, y, 0.08f) {
    var mode = GhostMode.CHASE
    var target = Vector2D(0f, 0f)

    fun updateWithPacman(map: GameMap, pacman: Pacman) {
        if (reachedTarget()) {
            updateTarget(map, pacman)
            val bestDir = findBestDirection(map)
            targetPos = currentPos + bestDir
        }
        super.update(map)
    }

    private fun findBestDirection(map: GameMap): Vector2D {
        val directions = arrayOf(
            Vector2D(1f, 0f), Vector2D(-1f, 0f),
            Vector2D(0f, 1f), Vector2D(0f, -1f)
        )

        var bestDir = Vector2D(0f, 0f)
        var bestDist = if (mode == GhostMode.FRIGHTENED) -1f else Float.MAX_VALUE

        for (dir in directions) {
            val nextX = (gridX + dir.x).toInt()
            val nextY = (gridY + dir.y).toInt()
            if (map.getTile(nextX, nextY) != com.example.pacdroidman.model.TileType.WALL) {
                val nextPos = Vector2D(nextX.toFloat(), nextY.toFloat())
                val dist = (target - nextPos).length()
                if (mode == GhostMode.FRIGHTENED) {
                    if (dist > bestDist) {
                        bestDist = dist
                        bestDir = dir
                    }
                } else {
                    if (dist < bestDist) {
                        bestDist = dist
                        bestDir = dir
                    }
                }
            }
        }
        return bestDir
    }

    open fun updateTarget(map: GameMap, pacman: Pacman) {
        // Default target is center of map or random
    }


    private fun reachedTarget(): Boolean {
        return (targetPos - currentPos).length() < speed
    }
}
