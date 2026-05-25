package com.example.pacdroidman.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceView
import com.example.pacdroidman.engine.GameEngine
import com.example.pacdroidman.model.TileType
import com.example.pacdroidman.utils.Vector2D
import kotlin.math.abs

class GameView(context: Context) : SurfaceView(context), Runnable {
    private var gameThread: Thread? = null
    private var running = false
    private val paint = Paint()
    val engine = GameEngine()
    private var tileSize = 0f
    private var offsetX = 0f
    private var offsetY = 0f
    private var downX = 0f
    private var downY = 0f
    private var mouthTimer = 0f

    init {
        paint.isAntiAlias = true
    }

    fun resume() {
        running = true
        gameThread = Thread(this)
        gameThread?.start()
    }

    fun pause() {
        running = false
        gameThread?.join()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        tileSize = Math.min(w.toFloat() / engine.map.width, h.toFloat() / engine.map.height)
        offsetX = (w - engine.map.width * tileSize) / 2f
        offsetY = (h - engine.map.height * tileSize) / 2f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            if (engine.state != GameEngine.GameState.PLAYING) {
                engine.restart()
                return true
            }

            val dx = event.x - downX
            val dy = event.y - downY

            if (abs(dx) > abs(dy)) {
                if (dx > 0) engine.pacman.changeDirection(Vector2D(1f, 0f))
                else engine.pacman.changeDirection(Vector2D(-1f, 0f))
            } else {
                if (dy > 0) engine.pacman.changeDirection(Vector2D(0f, 1f))
                else engine.pacman.changeDirection(Vector2D(0f, -1f))
            }
        } else if (event.action == MotionEvent.ACTION_DOWN) {
            downX = event.x
            downY = event.y
        }
        return true
    }

    override fun run() {
        while (running) {
            update()
            draw()
            Thread.sleep(16) // Target ~60 FPS
        }
    }

    private fun update() {
        engine.update()
        mouthTimer += 0.15f
        if (mouthTimer > Math.PI.toFloat()) mouthTimer -= Math.PI.toFloat()
    }

    private fun draw() {
        if (holder.surface == null) return
        val canvas: Canvas = holder.lockCanvas() ?: return

        canvas.drawColor(engine.currentTheme.background)

        // Draw Map
        for (y in 0 until engine.map.height) {
            for (x in 0 until engine.map.width) {
                val tile = engine.map.getTile(x, y)
                paint.color = when (tile) {
                    TileType.WALL -> engine.currentTheme.wall
                    TileType.PELLET -> engine.currentTheme.pellet
                    TileType.POWER_PELLET -> Color.parseColor("#FFB8B8FF")
                    TileType.EMPTY -> engine.currentTheme.background
                }

                if (tile != TileType.EMPTY) {
                    val left = x * tileSize + offsetX
                    val top = y * tileSize + offsetY
                    if (tile == TileType.WALL) {
                        canvas.drawRoundRect(left + 2f, top + 2f, left + tileSize - 2f, top + tileSize - 2f, 4f, 4f, paint)
                    } else {
                        val radius = if (tile == TileType.POWER_PELLET) tileSize * 0.25f else tileSize * 0.15f
                        canvas.drawCircle(left + tileSize/2, top + tileSize/2, radius, paint)
                    }
                }
            }
        }

        // Draw Ghosts
        engine.ghosts.forEach { ghost ->
            paint.color = if (ghost.mode == com.example.pacdroidman.entities.GhostMode.FRIGHTENED) {
                Color.parseColor("#2121FF")
            } else {
                ghost.color
            }
            val gx = ghost.currentPos.x * tileSize + offsetX
            val gy = ghost.currentPos.y * tileSize + offsetY

            // Ghost body
            canvas.drawRoundRect(gx + tileSize*0.1f, gy + tileSize*0.2f, gx + tileSize*0.9f, gy + tileSize*0.9f, 8f, 8f, paint)
            // Ghost head
            canvas.drawCircle(gx + tileSize/2, gy + tileSize*0.3f, tileSize*0.4f, paint)

            // Ghost eyes
            paint.color = Color.WHITE
            canvas.drawCircle(gx + tileSize*0.35f, gy + tileSize*0.3f, tileSize*0.08f, paint)
            canvas.drawCircle(gx + tileSize*0.65f, gy + tileSize*0.3f, tileSize*0.08f, paint)

            paint.color = Color.BLACK
            canvas.drawCircle(gx + tileSize*0.35f, gy + tileSize*0.3f, tileSize*0.04f, paint)
            canvas.drawCircle(gx + tileSize*0.65f, gy + tileSize*0.3f, tileSize*0.04f, paint)
        }

        // Draw Pacman
        paint.color = Color.YELLOW
        val px = engine.pacman.currentPos.x * tileSize + offsetX
        val py = engine.pacman.currentPos.y * tileSize + offsetY
        val centerX = px + tileSize/2
        val centerY = py + tileSize/2
        val radius = tileSize * 0.4f

        val mouthOpen = (Math.sin(mouthTimer.toDouble()) * 45.0 + 45.0).toFloat()
        val baseAngle = when {
            engine.pacman.direction.x > 0 -> 0f
            engine.pacman.direction.x < 0 -> 180f
            engine.pacman.direction.y > 0 -> 90f
            engine.pacman.direction.y < 0 -> 270f
            else -> 0f
        }

        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius,
            baseAngle + mouthOpen, 360f - 2 * mouthOpen, true, paint)

        // Draw Score
        paint.color = engine.currentTheme.text
        paint.textSize = 60f
        canvas.drawText("Score: ${engine.score}", 50f, 100f, paint)

        if (engine.state != GameEngine.GameState.PLAYING) {
            canvas.drawColor(Color.argb(150, 0, 0, 0))
            paint.color = Color.WHITE
            paint.textSize = 100f
            paint.textAlign = android.graphics.Paint.Align.CENTER

            val message = if (engine.state == GameEngine.GameState.WON) "YOU WIN!" else "GAME OVER"
            canvas.drawText(message, canvas.width / 2f, canvas.height / 2f, paint)

            paint.textSize = 60f
            canvas.drawText("Tap to Restart", canvas.width / 2f, canvas.height / 2f + 120f, paint)
            paint.textAlign = android.graphics.Paint.Align.LEFT
        }

        holder.unlockCanvasAndPost(canvas)
    }
}
