package com.example.pacdroidman.utils

data class Vector2D(val x: Float, val y: Float) {
    operator fun plus(other: Vector2D) = Vector2D(x + other.x, y + other.y)
    operator fun minus(other: Vector2D) = Vector2D(x - other.x, y - other.y)
    operator fun times(scalar: Float) = Vector2D(x * scalar, y * scalar)

    fun length() = Math.sqrt((x * x + y * y).toDouble()).toFloat()
    fun normalize(): Vector2D {
        val len = length()
        return if (len == 0f) Vector2D(0f, 0f) else Vector2D(x / len, y / len)
    }
}
