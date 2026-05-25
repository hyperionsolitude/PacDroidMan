package com.example.pacdroidman.entities

import com.example.pacdroidman.model.GameMap
import com.example.pacdroidman.utils.Vector2D

class Clyde(x: Int, y: Int) : Ghost(x, y, 0xFFC0C0C0.toInt()) {
    override fun updateTarget(map: GameMap, pacman: Pacman) {
        val dist = (pacman.currentPos - currentPos).length()
        if (dist > 8f) {
            target = Vector2D(pacman.currentPos.x, pacman.currentPos.y)
        } else {
            target = Vector2D(0f, 0f) // Retreat to corner
        }
    }
}
