package com.example.android.dicegameapplication

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.dicegameapplication.viewModel.DiceViewModel

class GameActivity : AppCompatActivity() {

    private lateinit var viewModel : DiceViewModel

    private val imageViewsForUser by lazy {
        arrayOf<ImageView> (
            findViewById(R.id.userDice1),
            findViewById(R.id.userDice2),
            findViewById(R.id.userDice3),
            findViewById(R.id.userDice4),
            findViewById(R.id.userDice5)
        )
    }

    private val imageViewsForRobot by lazy {
        arrayOf<ImageView> (
            findViewById(R.id.compDice1),
            findViewById(R.id.compDice2),
            findViewById(R.id.compDice3),
            findViewById(R.id.compDice4),
            findViewById(R.id.compDice5)
        )
    }

    private lateinit var btnThrow: Button

    private val textViewUserRollFullScore by lazy {
        findViewById<TextView>(R.id.textViewUserScore)
    }
    private val textViewRobotRollFullScore by lazy {
        findViewById<TextView>(R.id.textViewRobotScore)
    }

    private val textViewUserFullScore by lazy {
        findViewById<TextView>(R.id.textViewUserFullScore)
    }
    private val textViewRobotFullScore by lazy {
        findViewById<TextView>(R.id.textViewRobotFullScore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // initialize the view model
        viewModel = ViewModelProvider(this).get(DiceViewModel::class.java)

        btnThrow = findViewById(R.id.btnThrow)
        btnThrow.setOnClickListener{
            viewModel.rollDice()
        }

        // To subscribe to changes in a ViewModel
        viewModel.robotDiceArray.observe(this, Observer {
            updateDisplay(it,imageViewsForRobot) // it is the new value when it's published from the viewModel
        })
        viewModel.userDiceArray.observe(this, Observer {
            // lambda expression
            updateDisplay(it,imageViewsForUser)
        })
        viewModel.userCurrentRollFullScore.observe(this, Observer {
            textViewUserRollFullScore.text = it
        })
        viewModel.robotCurrentRollFullScore.observe(this, Observer {
            textViewRobotRollFullScore.text = it
        })
        viewModel.userFullScore.observe(this, Observer {
            textViewUserFullScore.text = it
        })
        viewModel.robotFullScore.observe(this, Observer {
            textViewRobotFullScore.text = it
        })

    }


    private fun updateDisplay(dice: IntArray, imageViews: Array<ImageView>) {
        // loop through image view objects and set the images one die at a time
        for (i in imageViews.indices) {
            // each time through the loop, create a variable "drawableId"
            // it wil contain a resource ID that's pointing to one of these six images
            val drawableId = when (dice[i]) {
                // look at the numeric value in that index position in the array return the respective drawableId
                1 -> R.drawable.die_face_1
                2 -> R.drawable.die_face_2
                3 -> R.drawable.die_face_3
                4 -> R.drawable.die_face_4
                5 -> R.drawable.die_face_5
                6 -> R.drawable.die_face_6
                else -> R.drawable.die_face_6
            }
            imageViews[i].setImageResource(drawableId)
        }
    }
}