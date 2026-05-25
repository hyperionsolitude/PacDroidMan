package com.example.pacdroidman.entities

import com.example.pacdroidman.model.GameMap
import com.example.pacdroidman.utils.Vector2D

class Blinky(x: Int, y: Int) : Ghost(x, y, 0xFFB60000.toInt()) {
    override fun updateTarget(map: GameMap, pacman: Pacman) {
        target = Vector2D(pacman.currentPos.x, pacman.currentPos.y)
    }
}
