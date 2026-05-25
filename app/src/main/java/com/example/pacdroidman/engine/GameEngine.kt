package com.example.pacdroidman.engine

import android.graphics.Color
import com.example.pacdroidman.entities.*
import com.example.pacdroidman.model.GameMap
import com.example.pacdroidman.model.TileType
import com.example.pacdroidman.utils.Vector2D

class GameEngine {
    enum class GameState { PLAYING, GAME_OVER, WON }
    var state = GameState.PLAYING
    private var frightenedTimer = 0

    data class Theme(
        val background: Int,
        val wall: Int,
        val pellet: Int,
        val text: Int
    )

    val darkTheme = Theme(
        background = Color.BLACK,
        wall = Color.parseColor("#2121FF"),
        pellet = Color.WHITE,
        text = Color.WHITE
    )

    val lightTheme = Theme(
        background = Color.parseColor("#D3D3D3"),
        wall = Color.parseColor("#4B0082"),
        pellet = Color.parseColor("#FFD700"),
        text = Color.BLACK
    )

    var currentTheme = darkTheme

    fun toggleTheme() {
        currentTheme = if (currentTheme == darkTheme) lightTheme else darkTheme
    }

    val map = GameMap()
    val pacman = Pacman(1, 1)
    val blinky = Blinky(10, 10)
    val pinky = Pinky(11, 10)
    val inky = Inky(10, 11, blinky)
    val clyde = Clyde(11, 11)
    val ghosts = listOf(blinky, pinky, inky, clyde)
    var score = 0

    fun update() {
        if (state != GameState.PLAYING) return
        pacman.update(map)
        ghosts.forEach { it.updateWithPacman(map, pacman) }
        checkCollisions()
        checkWinCondition()
        updateFrightenedTimer()
    }

    private fun updateFrightenedTimer() {
        if (frightenedTimer > 0) {
            frightenedTimer--
            if (frightenedTimer == 0) {
                ghosts.forEach { it.mode = GhostMode.CHASE }
            }
        }
    }

    private fun checkWinCondition() {
        if (map.tiles.all { row -> row.all { it != TileType.PELLET && it != TileType.POWER_PELLET } }) {
            state = GameState.WON
        }
    }

    private fun checkCollisions() {
        val gx = pacman.currentPos.x.toInt()
        val gy = pacman.currentPos.y.toInt()
        val tile = map.getTile(gx, gy)
        if (tile == TileType.PELLET) {
            map.tiles[gy][gx] = TileType.EMPTY
            score += 10
        } else if (tile == TileType.POWER_PELLET) {
            map.tiles[gy][gx] = TileType.EMPTY
            score += 50
            frightenedTimer = 600 // ~10 seconds at 60fps
            ghosts.forEach { it.mode = GhostMode.FRIGHTENED }
        }

        ghosts.forEach { ghost ->
            if ((ghost.currentPos - pacman.currentPos).length() < 0.5f) {
                if (ghost.mode == GhostMode.FRIGHTENED) {
                    score += 200
                    ghost.currentPos = Vector2D(10f, 10f) // Return to spawn
                    ghost.targetPos = Vector2D(10f, 10f)
                } else {
                    state = GameState.GAME_OVER
                }
            }
        }
    }

    fun restart() {
        score = 0
        state = GameState.PLAYING
        frightenedTimer = 0
        pacman.currentPos = Vector2D(1f, 1f)
        pacman.targetPos = Vector2D(1f, 1f)
        pacman.direction = Vector2D(0f, 0f)
        pacman.nextDirection = Vector2D(0f, 0f)

        blinky.currentPos = Vector2D(10f, 10f)
        blinky.targetPos = Vector2D(10f, 10f)
        blinky.mode = GhostMode.CHASE

        pinky.currentPos = Vector2D(11f, 10f)
        pinky.targetPos = Vector2D(11f, 10f)
        pinky.mode = GhostMode.CHASE

        inky.currentPos = Vector2D(10f, 11f)
        inky.targetPos = Vector2D(10f, 11f)
        inky.mode = GhostMode.CHASE

        clyde.currentPos = Vector2D(11f, 11f)
        clyde.targetPos = Vector2D(11f, 11f)
        clyde.mode = GhostMode.CHASE

        map.setupMaze()
    }
}
