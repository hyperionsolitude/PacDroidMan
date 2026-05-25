package com.example.pacdroidman.entities

import com.example.pacdroidman.model.GameMap
import com.example.pacdroidman.utils.Vector2D

class Pinky(x: Int, y: Int) : Ghost(x, y, 0xFFFFB8FF.toInt()) {
    override fun updateTarget(map: GameMap, pacman: Pacman) {
        // Pinky targets 4 tiles ahead of Pacman
        val ambushX = pacman.currentPos.x + pacman.direction.x * 4
        val ambushY = pacman.currentPos.y + pacman.direction.y * 4
        target = Vector2D(ambushX, ambushY)
    }
}
