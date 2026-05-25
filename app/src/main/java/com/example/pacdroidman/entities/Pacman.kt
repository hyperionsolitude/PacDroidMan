package com.example.pacdroidman.entities

import com.example.pacdroidman.model.GameMap
import com.example.pacdroidman.utils.Vector2D

class Pacman(x: Int, y: Int) : Entity(x, y, 0.1f) {
    var nextDirection = Vector2D(0f, 0f)

    fun changeDirection(dir: Vector2D) {
        nextDirection = dir
    }

    override fun update(map: GameMap) {
        // Try to change direction if Pacman is at a grid intersection
        if (reachedTarget()) {
            if (canMove(map, nextDirection)) {
                direction = nextDirection
            } else if (!canMove(map, direction)) {
                direction = Vector2D(0f, 0f)
            }

            if (direction != Vector2D(0f, 0f)) {
                targetPos = currentPos + direction
            }
        }
        super.update(map)
    }

    private fun canMove(map: GameMap, dir: Vector2D): Boolean {
        val nextX = (gridX + dir.x).toInt()
        val nextY = (gridY + dir.y).toInt()
        return map.getTile(nextX, nextY) != com.example.pacdroidman.model.TileType.WALL
    }

    private fun reachedTarget(): Boolean {
        return (targetPos - currentPos).length() < speed
    }
}
