package com.arithmetic.arithmeticgame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.CountDownTimer
import android.support.v7.widget.AppCompatTextView
import android.view.View
import android.widget.Button
import com.arithmetic.arithmeticgame.utils.Value
import java.util.concurrent.TimeUnit

class GameActivity : AppCompatActivity() {

    private lateinit var equationOne: AppCompatTextView
    private lateinit var equationOneSolution: AppCompatTextView
    private lateinit var equationTwo: AppCompatTextView
    private lateinit var equationTwoSolution: AppCompatTextView
    private lateinit var correctMessage: AppCompatTextView
    private lateinit var wrongMessage: AppCompatTextView

    private var countCorrects = 0
    private var countWrongs = 0

    private lateinit var timeCounter: AppCompatTextView
    private lateinit var greaterButton: Button
    private lateinit var equalsButton: Button
    private var result = ""
    private lateinit var lessButton: Button
    private var operators = setOf("+", "-", "*", "/")

    var timer: Timer? = null

    //Call this method to start timer on activity start
    private fun startTimer() {
        timer = Timer(50000)
        timer?.start()
    }

    //Call this method to update the timer
    private fun updateTimer() {
        if (timer != null) {
            // 5 10 15 .. And Different of 0
            if (countCorrects % 5 == 0 && countCorrects != 0) {
                val miliis = timer?.millisUntilFinished?.plus(TimeUnit.SECONDS.toMillis(10))
                //Here you need to maintain single instance for previous
                timer?.cancel()
                timer = miliis?.let { Timer(it) }
                timer?.start()
            } else {
                val miliis = timer?.millisUntilFinished
                //Here you need to maintain single instance for previous
                timer?.cancel()
                timer = miliis?.let { Timer(it) }
                timer?.start()
            }
        } else {
            startTimer()
        }
    }

    inner class Timer(miliis: Long) : CountDownTimer(miliis, 1000) {
        var millisUntilFinished: Long = 0
        override fun onFinish() {
            timeCounter.text = "Time Left : 0"
            printDialog("")
        }

        override fun onTick(millisUntilFinished: Long) {
            this.millisUntilFinished = millisUntilFinished
            timeCounter.text = "Time Left : " + millisUntilFinished / 1000
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        equationOne = findViewById(R.id.equation_one)
        equationOneSolution = findViewById(R.id.equation_one_solution)
        equationTwo = findViewById(R.id.equation_two)
        equationTwoSolution = findViewById(R.id.equation_two_solution)
        greaterButton = findViewById(R.id.greater_button)
        equalsButton = findViewById(R.id.equals_button)
        lessButton = findViewById(R.id.less_button)
        timeCounter = findViewById(R.id.time_counter)
        correctMessage = findViewById(R.id.correct_message)
        wrongMessage = findViewById(R.id.wrong_message)

        supportActionBar!!.hide()

        generateExpressions()
        startTimer()

        greaterButton.setOnClickListener {
            result = getResult(
                "greater",
                equationOneSolution.text.toString().toFloat(),
                equationTwoSolution.text.toString().toFloat()
            )
            printDialog(result)
        }

        equalsButton.setOnClickListener {
            result = getResult(
                "equals",
                equationOneSolution.text.toString().toFloat(),
                equationTwoSolution.text.toString().toFloat()
            )
            printDialog(result)
        }

        lessButton.setOnClickListener {
            result = getResult(
                "less",
                equationOneSolution.text.toString().toFloat(),
                equationTwoSolution.text.toString().toFloat()
            )
            printDialog(result)
        }
    }

    private fun generateExpressions() {
        var equation1 = randomExpression()
        var result1 = Value(equation1).resolve()

        var equation2 = randomExpression()
        var result2 = Value(equation2).resolve()

        while (result1 > 100 || result2 > 100) {
            equation1 = randomExpression()
            result1 = Value(equation1).resolve()

            equation2 = randomExpression()
            result2 = Value(equation2).resolve()
        }

        equationOne.text = equation1
        equationOneSolution.text = result1.toString()
        equationTwo.text = equation2
        equationTwoSolution.text = result2.toString()
    }

    fun isWhole(value: Double): Boolean {
        return value - value.toInt() == 0.0
    }

    private fun getResult(
        buttonType: String,
        equationOneAnswer: Float,
        equationTwoAnswer: Float
    ): String {
        return if (buttonType == "greater" && equationOneAnswer > equationTwoAnswer) {
            "CORRECT!"
        } else if (buttonType == "equals" && equationOneAnswer == equationTwoAnswer) {
            "CORRECT!"
        } else if (buttonType == "less" && equationOneAnswer < equationTwoAnswer) {
            "CORRECT!"
        } else {
            "WRONG!"
        }
    }

    fun printDialog(result: String) {
        // Stop The Timer
        timer?.cancel()

        // Show The Dialog
        var resultMessage = ""
        if (result == "CORRECT!") {
            resultMessage = "CORRECT!"
            correctMessage.visibility = View.VISIBLE
            countCorrects++
        } else if (result == "WRONG!") {
            countWrongs++
            wrongMessage.visibility = View.VISIBLE
            resultMessage = "WRONG!"
        } else {
            resultMessage =
                "No More Time!\nCorrect Answers: ${countCorrects}\nWrong Answers: $countWrongs"
        }

        val alertDialog: AlertDialog? = this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton("ok",
                    DialogInterface.OnClickListener { _, _ ->
                        if (result == "CORRECT!") {
                            correctMessage.visibility = View.GONE
                            updateTimer()
                            generateExpressions()
                        } else if (result == "WRONG!") {
                            generateExpressions()
                            wrongMessage.visibility = View.GONE
                            updateTimer()
                        } else {
                            // Go to the Game Over Activity
                            timeCounter.text = "GAME OVER!"
                            greaterButton.visibility = View.GONE
                            lessButton.visibility = View.GONE
                            equalsButton.visibility = View.GONE
                        }
                    })
            }

            builder.setMessage(resultMessage)
            // Create the AlertDialog
            builder.create()
        }

        alertDialog!!.show()
    }

    override fun finish() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    fun randomNumber(): Int = (1..20).random()

    fun randomExpression(): String {
        var termCount = (1..4).random()
        var equation = ""
        var term1 = ""
        var term2 = ""
        var term3 = ""
        var term4 = ""
        var operation1 = ""
        var operation2 = ""
        var operation3 = ""
        var operation4 = ""

        termCount = 3
        if (termCount == 1) {
            equation = randomNumber().toString()
        } else if (termCount == 2) {
            // Reminder of equation must be 0
            term1 = randomNumber().toString()
            term2 = randomNumber().toString()
            equation = "$term1${operators.random()}$term2"

            while (!isWhole(Value(equation).resolve().toDouble()) || Value(equation).resolve()
                    .toInt() > 100
            ) {
                term1 = randomNumber().toString()
                term2 = randomNumber().toString()
                equation = "$term1${operators.random()}$term2"
            }
        } else if (termCount == 3) {
            term1 = randomNumber().toString()
            term2 = randomNumber().toString()
            term3 = randomNumber().toString()
            operation1 = "$term1${operators.random()}$term2"
            operation2 = "($operation1)${operators.random()}$term3"

            while (!isWhole(Value(operation1).resolve().toDouble()) || Value(operation1).resolve()
                    .toInt() > 100
            ) {
                term1 = randomNumber().toString()
                term2 = randomNumber().toString()
                operation1 = "$term1${operators.random()}$term2"
            }

            while (!isWhole(Value(operation2).resolve().toDouble()) || Value(operation2).resolve()
                    .toInt() > 100
            ) {
                term3 = randomNumber().toString()
                operation2 = "($operation1)${operators.random()}$term3"
            }

            equation = operation2

        } else if (termCount == 4) {
            term1 = randomNumber().toString()
            term2 = randomNumber().toString()
            term3 = randomNumber().toString()
            term4 = randomNumber().toString()
            operation1 = "$term1${operators.random()}$term2"
            operation2 = "($operation1)${operators.random()}$term3"
            operation3 = "($operation2)${operators.random()}$term4"

            while (!isWhole(Value(operation1).resolve().toDouble()) || Value(operation1).resolve()
                    .toInt() > 100
            ) {
                term1 = randomNumber().toString()
                term2 = randomNumber().toString()
                operation1 = "$term1${operators.random()}$term2"
            }

            while (!isWhole(Value(operation2).resolve().toDouble()) || Value(operation2).resolve()
                    .toInt() > 100
            ) {
                term3 = randomNumber().toString()
                operation2 = "($operation1)${operators.random()}$term3"
            }

            while (!isWhole(Value(operation3).resolve().toDouble()) || Value(operation3).resolve()
                    .toInt() > 100
            ) {
                term4 = randomNumber().toString()
                operation3 = "($operation2)${operators.random()}$term4"
            }

            equation = operation3
        }

        // Remove Blank Spaces
        equation = equation.replace(" ", "")
        return equation
    }

}

