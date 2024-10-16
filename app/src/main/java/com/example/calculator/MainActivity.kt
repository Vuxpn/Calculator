package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private var currentInput = StringBuilder()
    private var currentOperator: String? = null
    private var firstOperand: BigDecimal? = null
    private var lastNumeric = false
    private var lastDot = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tv_display)

        setNumberButtonListeners()
        setOperatorButtonListeners()
        setOtherButtonListeners()
    }

    private fun setNumberButtonListeners() {
        val numberButtons = listOf(
            R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
            R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9
        )

        numberButtons.forEach { id ->
            findViewById<Button>(id).setOnClickListener { onDigit(it as Button) }
        }
    }

    private fun setOperatorButtonListeners() {
        val operatorButtons = listOf(
            R.id.btn_add, R.id.btn_sub, R.id.btn_mul, R.id.btn_div
        )

        operatorButtons.forEach { id ->
            findViewById<Button>(id).setOnClickListener { onOperator(it as Button) }
        }
    }

    private fun setOtherButtonListeners() {
        findViewById<Button>(R.id.btn_dot).setOnClickListener { onDecimalPoint() }
        findViewById<Button>(R.id.btn_equal).setOnClickListener { onEqual() }
        findViewById<Button>(R.id.btn_c).setOnClickListener { onClear() }
        findViewById<Button>(R.id.btn_ce).setOnClickListener { onClear() }
        findViewById<Button>(R.id.btn_bs).setOnClickListener { onBackspace() }
        findViewById<Button>(R.id.btn_neg).setOnClickListener { onNegative() }
    }

    private fun onDigit(button: Button) {
        currentInput.append(button.text)
        tvDisplay.text = currentInput.toString()
        lastNumeric = true
    }

    private fun onOperator(button: Button) {
        if (lastNumeric) {
            if (firstOperand == null) {
                firstOperand = BigDecimal(currentInput.toString())
            } else if (currentOperator != null) {
                val secondOperand = BigDecimal(currentInput.toString())
                firstOperand = performOperation(firstOperand!!, secondOperand, currentOperator!!)
                tvDisplay.text = firstOperand.toString()
            }
            currentOperator = button.text.toString()
            currentInput.clear()
            lastNumeric = false
            lastDot = false
        }
    }

    private fun onDecimalPoint() {
        if (lastNumeric && !lastDot) {
            currentInput.append(".")
            tvDisplay.text = currentInput.toString()
            lastNumeric = false
            lastDot = true
        }
    }

    private fun onClear() {
        currentInput.clear()
        firstOperand = null
        currentOperator = null
        tvDisplay.text = "0"
        lastNumeric = false
        lastDot = false
    }

    private fun onBackspace() {
        if (currentInput.isNotEmpty()) {
            currentInput.deleteCharAt(currentInput.length - 1)
            tvDisplay.text = if (currentInput.isEmpty()) "0" else currentInput.toString()
        }
        if (currentInput.isEmpty()) {
            lastNumeric = false
        }
    }

    private fun onNegative() {
        if (currentInput.isNotEmpty() && currentInput.toString() != "0") {
            if (currentInput[0] == '-') {
                currentInput.deleteCharAt(0)
            } else {
                currentInput.insert(0, '-')
            }
            tvDisplay.text = currentInput.toString()
        }
    }

    private fun onEqual() {
        if (lastNumeric && firstOperand != null && currentOperator != null) {
            val secondOperand = BigDecimal(currentInput.toString())
            val result = performOperation(firstOperand!!, secondOperand, currentOperator!!)
            tvDisplay.text = result.toString()
            firstOperand = result
            currentInput.clear()
            currentInput.append(result)
            currentOperator = null
        }
    }

    private fun performOperation(a: BigDecimal, b: BigDecimal, operator: String): BigDecimal {
        return when (operator) {
            "+" -> a.add(b)
            "-" -> a.subtract(b)
            "x" -> a.multiply(b)
            "/" -> if (b.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else a.divide(b, 8, RoundingMode.HALF_UP)
            else -> throw IllegalArgumentException("Invalid operator")
        }
    }
}