package com.calculator

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // ── Engine ─────────────────────────────────────────
    private val engine = CalculatorEngine()

    // ── Views ──────────────────────────────────────────
    private lateinit var tvResult: TextView
    private lateinit var tvExpression: TextView
    private lateinit var tvStatus: TextView

    // ── Lifecycle ──────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        bindButtons()
    }

    // ── View Binding ───────────────────────────────────
    private fun bindViews() {
        tvResult     = findViewById(R.id.tvResult)
        tvExpression = findViewById(R.id.tvExpression)
        tvStatus     = findViewById(R.id.tvStatus)
    }

    private fun bindButtons() {

        // Number buttons
        val numberMap = mapOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2",
            R.id.btn3 to "3", R.id.btn4 to "4", R.id.btn5 to "5",
            R.id.btn6 to "6", R.id.btn7 to "7", R.id.btn8 to "8",
            R.id.btn9 to "9"
        )
        numberMap.forEach { (id, digit) ->
            findViewById<Button>(id).setOnClickListener {
                animatePress(it)
                render(engine.inputDigit(digit))
            }
        }

        // Decimal
        findViewById<Button>(R.id.btnDot).setOnClickListener {
            animatePress(it)
            render(engine.inputDecimal())
        }

        // Operator buttons
        val operatorMap = mapOf(
            R.id.btnPlus     to "+",
            R.id.btnMinus    to "−",
            R.id.btnMultiply to "×",
            R.id.btnDivide   to "÷"
        )
        operatorMap.forEach { (id, op) ->
            findViewById<Button>(id).setOnClickListener {
                animatePress(it)
                render(engine.inputOperator(op))
                highlightOperator(id)
            }
        }

        // Equals
        findViewById<Button>(R.id.btnEquals).setOnClickListener {
            animatePress(it)
            clearOperatorHighlights()
            render(engine.evaluate())
        }

        // Clear
        findViewById<Button>(R.id.btnClear).setOnClickListener {
            animatePress(it)
            clearOperatorHighlights()
            render(engine.clear())
        }

        // Negate
        findViewById<Button>(R.id.btnNegate).setOnClickListener {
            animatePress(it)
            render(engine.negate())
        }

        // Percent
        findViewById<Button>(R.id.btnPercent).setOnClickListener {
            animatePress(it)
            render(engine.percent())
        }
    }

    // ── Render ─────────────────────────────────────────
    /**
     * Takes an EngineResult and updates all TextViews accordingly.
     */
    private fun render(result: EngineResult) {
        // Update main display
        tvResult.text = result.display
        autoResizeDisplay(result.display.length)

        // Update expression line
        tvExpression.text = result.expression

        // Update status / error
        tvStatus.text = if (result.isError) result.errorMsg else ""

        if (result.isError) shakeDisplay()
    }

    // ── Auto-resize display text ───────────────────────
    private fun autoResizeDisplay(length: Int) {
        tvResult.textSize = when {
            length > 12 -> 28f
            length > 8  -> 40f
            else        -> 56f
        }
    }

    // ── Operator Highlight ─────────────────────────────
    private val operatorIds = listOf(
        R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide
    )

    private fun highlightOperator(activeId: Int) {
        operatorIds.forEach { id ->
            val btn = findViewById<Button>(id)
            btn.alpha = if (id == activeId) 0.5f else 1.0f
        }
    }

    private fun clearOperatorHighlights() {
        operatorIds.forEach { id ->
            findViewById<Button>(id).alpha = 1.0f
        }
    }

    // ── Animations ─────────────────────────────────────
    private fun animatePress(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0.92f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0.92f, 1f)
        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            duration = 120
            start()
        }
    }

    private fun shakeDisplay() {
        ObjectAnimator.ofFloat(tvResult, View.TRANSLATION_X,
            0f, -16f, 16f, -10f, 10f, -6f, 6f, 0f
        ).apply {
            duration = 400
            start()
        }
    }
}