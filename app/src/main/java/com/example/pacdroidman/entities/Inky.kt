package com.example.pacdroidman.entities

import com.example.pacdroidman.model.GameMap
import com.example.pacdroidman.utils.Vector2D

class Inky(x: Int, y: Int, private val blinky: Blinky) : Ghost(x, y, 0xFF00FFFF.toInt()) {
    override fun updateTarget(map: GameMap, pacman: Pacman) {
        // Inky uses Blinky's position to flank Pacman
        val vector = pacman.currentPos - blinky.currentPos
        val flankX = pacman.currentPos.x + vector.x * 2
        val flankY = pacman.currentPos.y + vector.y * 2
        target = Vector2D(flankX, flankY)
    }
}
