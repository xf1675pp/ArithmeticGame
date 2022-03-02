package com.arithmetic.arithmeticgame

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var newGameButton: Button
    private lateinit var aboutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hide Toolbar
        supportActionBar!!.hide()

        newGameButton = findViewById(R.id.new_game_button)
        aboutButton = findViewById(R.id.about_button)

        aboutButton.setOnClickListener {
            showAboutPopup()
        }

        newGameButton.setOnClickListener {
            startNewGame()
        }
    }

    private fun showAboutPopup() {
        val textAbout = this.getString(R.string.about_text)

        val alertDialog: AlertDialog? = this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton("ok",
                    DialogInterface.OnClickListener { _, _ ->

                    })
            }

            builder.setMessage(textAbout)
            // Create the AlertDialog
            builder.create()
        }

        alertDialog!!.show()

    }

    private fun startNewGame() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }
}