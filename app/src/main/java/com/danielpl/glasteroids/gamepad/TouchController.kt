package com.danielpl.glasteroids.gamepad

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import com.danielpl.glasteroids.R
import com.danielpl.glasteroids.util.Config.isGameOver
import com.danielpl.glasteroids.util.Config.isLevelSuccessful
import com.danielpl.glasteroids.util.Config.restart

@SuppressLint("ClickableViewAccessibility")
class TouchController(view: View) : InputManager(),
    View.OnTouchListener {
    init {
        view.findViewById<Button>(R.id.keypad_left).setOnTouchListener(this)
        view.findViewById<Button>(R.id.keypad_right).setOnTouchListener(this)
        view.findViewById<Button>(R.id.keypad_a).setOnTouchListener(this)
        view.findViewById<Button>(R.id.keypad_b).setOnTouchListener(this)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val action = event.actionMasked
        val id: Int = v.id
        if (action == MotionEvent.ACTION_DOWN) {
            // User started pressing a key
            if (id == R.id.keypad_left) {
                horizontalFactor -= 1f
            } else if (id == R.id.keypad_right) {
                horizontalFactor += 1f
            }
            if (id == R.id.keypad_a) {
                pressingA = true
            }
            if (id == R.id.keypad_b) {
                pressingB = true
            }
            if (isGameOver || isLevelSuccessful) {
                restart = true
            }
        } else if (action == MotionEvent.ACTION_UP) {
            // User released a key
            if (id == R.id.keypad_left) {
                horizontalFactor += 1f
            } else if (id == R.id.keypad_right) {
                horizontalFactor -= 1f
            }
            if (id == R.id.keypad_a) {
                pressingA = false
            }
            if (id == R.id.keypad_b) {
                pressingB = false
            }
        }
        return false
    }
}