package com.example.stopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.view.MotionEvent

class MainActivity : AppCompatActivity() {

    private lateinit var timerText: TextView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var btnReset: Button

    private lateinit var btnHold : Button

    private var isRunning = false
    private var timeInMillis: Long = 0L

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerText = findViewById(R.id.timerText)
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        btnReset = findViewById(R.id.btnReset)
        btnHold = findViewById(R.id.btnHold)

        runnable = object : Runnable {
            override fun run() {
                timeInMillis += 10
                timerText.text = formatTime(timeInMillis)
                handler.postDelayed(this, 16)
            }
        }

        btnStart.setOnClickListener {
            if (!isRunning) {
                handler.post(runnable)
                isRunning = true
            }
        }

        btnStop.setOnClickListener {
            if (isRunning) {
                handler.removeCallbacks(runnable)
                isRunning = false
            }
        }
        btnHold.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    if (isRunning) {
                        handler.removeCallbacks(runnable)
                            isRunning = false
                        }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    v.performClick()
                    if (!isRunning) {
                        handler.post(runnable)
                        isRunning = true
                    }
                    true
                }
                else -> false
            }
        }

        btnReset.setOnClickListener {
            handler.removeCallbacks(runnable)
            isRunning = false
            timeInMillis = 0
            timerText.text = "00:00:00"
        }
    }

    private fun formatTime(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / 1000) / 60
        val millis = (ms % 1000) / 10
        return String.format("%02d:%02d:%02d", minutes, seconds, millis)
    }
}