package com.example.pacdroidman

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.pacdroidman.view.GameView

class MainActivity : AppCompatActivity() {
    private lateinit var gameView: GameView
    private lateinit var engine: com.example.pacdroidman.engine.GameEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a FrameLayout to hold both the GameView and the Theme Button
        val rootLayout = FrameLayout(this)

        gameView = GameView(this)

        // Need a way to access the engine from GameView or set it here
        // Since GameView currently instantiates its own engine,
        // we might need to change that to a shared instance or pass it in.

        rootLayout.addView(gameView)

        val themeButton = Button(this).apply {
            text = "Switch Theme"
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                setMargins(0, 50, 50, 0)
            }
            setOnClickListener {
                gameView.engine.toggleTheme()
            }
        }
        rootLayout.addView(themeButton)

        setContentView(rootLayout)
    }

    override fun onResume() {
        super.onResume()
        gameView.resume()
    }

    override fun onPause() {
        super.onPause()
        gameView.pause()
    }
}
