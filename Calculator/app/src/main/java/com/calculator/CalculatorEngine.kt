package com.calculator

/**
 * Pure calculation engine — no Android dependencies.
 * Fully unit-testable.
 */
class CalculatorEngine {

    // ── State ──────────────────────────────────────────
    private var currentInput: String = "0"
    private var previousInput: String = ""
    private var pendingOperator: String = ""
    private var justEvaluated: Boolean = false
    private var operatorJustPressed: Boolean = false  // tracks if operator was just pressed

    // ── Public State Accessors ─────────────────────────
    val display: String get() = currentInput
    val expression: String get() = if (pendingOperator.isNotEmpty() && previousInput.isNotEmpty())
        "$previousInput $pendingOperator" else ""
    val currentOperator: String get() = pendingOperator

    // ── Input Handling ─────────────────────────────────

    /** Called when a digit (0–9) is tapped. */
    fun inputDigit(digit: String): EngineResult {
        if (justEvaluated) {
            currentInput = digit
            previousInput = ""
            pendingOperator = ""
            justEvaluated = false         // fixed: was incorrectly set to true
            operatorJustPressed = false
        } else if (operatorJustPressed) { // after operator, start fresh input
            currentInput = digit
            operatorJustPressed = false
        } else if (currentInput == "0" && digit != ".") {
            currentInput = digit
        } else if (currentInput.length < 16) {
            currentInput += digit
        }
        return EngineResult(display = currentInput, expression = expression)
    }

    /** Called when decimal point is tapped. */
    fun inputDecimal(): EngineResult {
        if (justEvaluated || operatorJustPressed) {
            currentInput = "0."
            justEvaluated = false
            operatorJustPressed = false
        } else if (!currentInput.contains(".")) {
            currentInput += "."
        }
        return EngineResult(display = currentInput, expression = expression)
    }

    /** Called when an operator (+, −, ×, ÷) is tapped. */
    fun inputOperator(op: String): EngineResult {
        // Chain: if operator already pending, evaluate first
        if (pendingOperator.isNotEmpty() && !justEvaluated) {
            val chained = calculate()
            if (chained.isError) return chained
        }
        previousInput = currentInput
        pendingOperator = op
        justEvaluated = false
        operatorJustPressed = true  // flag that next digit should start fresh
        return EngineResult(display = currentInput, expression = expression)
    }

    /** Called when = is tapped. */
    fun evaluate(): EngineResult {
        if (pendingOperator.isEmpty() || previousInput.isEmpty()) {
            return EngineResult(display = currentInput, expression = expression)
        }
        return calculate()
    }

    /** AC — full reset. */
    fun clear(): EngineResult {
        currentInput        = "0"
        previousInput       = ""
        pendingOperator     = ""
        justEvaluated       = false
        operatorJustPressed = false
        return EngineResult(display = "0", expression = "")
    }

    /** +/− toggle sign. */
    fun negate(): EngineResult {
        if (currentInput == "0") return EngineResult(display = currentInput, expression = expression)
        currentInput = if (currentInput.startsWith("-"))
            currentInput.removePrefix("-")
        else
            "-$currentInput"
        return EngineResult(display = currentInput, expression = expression)
    }

    /** % — divide current value by 100. */
    fun percent(): EngineResult {
        val value = currentInput.toDoubleOrNull() ?: return EngineResult(display = currentInput, expression = expression)
        currentInput = formatResult(value / 100.0)
        return EngineResult(display = currentInput, expression = expression)
    }

    // ── Private Calculation Core ───────────────────────

    private fun calculate(): EngineResult {
        val a = previousInput.toDoubleOrNull()
        val b = currentInput.toDoubleOrNull()

        if (a == null || b == null) {
            return EngineResult(display = "Error", expression = "", isError = true, errorMsg = "Invalid input")
        }

        if (pendingOperator == "÷" && b == 0.0) {
            val err = EngineResult(display = "Error", expression = "", isError = true, errorMsg = "Division by zero")
            clear()
            return err
        }

        val result: Double = when (pendingOperator) {
            "+"  -> a + b
            "−"  -> a - b
            "×"  -> a * b
            "÷"  -> a / b
            else -> b
        }

        val expr = "$previousInput $pendingOperator $currentInput ="
        val resultStr = formatResult(result)

        // Update state for chaining
        currentInput        = resultStr
        previousInput       = ""
        pendingOperator     = ""
        justEvaluated       = true
        operatorJustPressed = false

        return EngineResult(
            display    = resultStr,
            expression = expr,
            isResult   = true
        )
    }

    /** Formats a Double: strips unnecessary trailing zeros. */
    private fun formatResult(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "Error"
        // Use toPrecision-style: max 10 significant digits
        val formatted = "%.10g".format(value)
        return if (formatted.contains('.'))
            formatted.trimEnd('0').trimEnd('.')
        else
            formatted
    }
}

/** Immutable result object returned by every engine operation. */
data class EngineResult(
    val display: String,
    val expression: String,
    val isResult: Boolean = false,
    val isError: Boolean = false,
    val errorMsg: String = ""
)