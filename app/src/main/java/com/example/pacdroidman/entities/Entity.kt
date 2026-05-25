package com.example.pacdroidman.entities

import com.example.pacdroidman.model.GameMap
import com.example.pacdroidman.utils.Vector2D

abstract class Entity(
    var gridX: Int,
    var gridY: Int,
    var speed: Float
) {
    var currentPos = Vector2D(gridX.toFloat(), gridY.toFloat())
    var targetPos = Vector2D(gridX.toFloat(), gridY.toFloat())
    var direction = Vector2D(0f, 0f)

    open fun update(map: GameMap) {
        moveTowardsTarget()
        if (reachedTarget()) {
            snapToGrid()
        }
    }

    private fun moveTowardsTarget() {
        val diff = targetPos - currentPos
        val dist = diff.length()
        if (dist > 0) {
            val step = diff.normalize() * speed
            currentPos += step
        }
    }

    private fun reachedTarget(): Boolean {
        return (targetPos - currentPos).length() < speed
    }

    private fun snapToGrid() {
        currentPos = Vector2D(targetPos.x, targetPos.y)
        gridX = targetPos.x.toInt()
        gridY = targetPos.y.toInt()
    }
}
