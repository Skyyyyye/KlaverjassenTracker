package com.example.klaverjassen

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge


val helper:Helper = Helper()

class MainActivity : ComponentActivity() {

    //set up variables
    private var team1Points: Int = 0
    private var team2Points: Int = 0

    private var tempBonus1: Int = 0
    private var tempBonus2: Int = 0

    private var round:Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //Set up buttons
        val bonusButton1:Button = findViewById<Button>(R.id.Bonus1)
        val bonusButton2:Button = findViewById<Button>(R.id.Bonus2)
        val remBonusButton1:Button = findViewById<Button>(R.id.remBonus1)
        val remBonusButton2:Button = findViewById<Button>(R.id.remBonus2)
        val name1: EditText = findViewById<EditText>(R.id.Name1)
        val name2: EditText = findViewById<EditText>(R.id.Name2)
        name1.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                name1.clearFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(name1.windowToken, 0)
                return@OnKeyListener true
            }
            false
        })
        name2.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                name2.clearFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(name2.windowToken, 0)
                return@OnKeyListener true
            }
            false
        })

        bonusButton1.setOnClickListener()
        {
            tempBonus1 = AddTempBonus(1,tempBonus1, true)
        }
        bonusButton2.setOnClickListener()
        {
            tempBonus2 = AddTempBonus(2,tempBonus2,true)
        }
        remBonusButton1.setOnClickListener()
        {
            tempBonus1 = AddTempBonus(1,tempBonus1,false)
        }
        remBonusButton2.setOnClickListener()
        {
            tempBonus2 = AddTempBonus(2,tempBonus2, false)
        }

        val addButton1:Button = findViewById<Button>(R.id.Addpoints1)
        val addButton2:Button = findViewById<Button>(R.id.Addpoints2)
        addButton1.setOnClickListener()
        {
            EndRound(1)
        }
        addButton2.setOnClickListener()
        {
            EndRound(2)
        }

        //set startup values
        findViewById<TextView>( R.id.CurrentBonus1).text = String.format(this.getString(R.string.currentBonus), 0)
        findViewById<TextView>( R.id.CurrentBonus2).text = String.format(this.getString(R.string.currentBonus), 0)
    }

    private fun AddPointsToTeam(team:Int, roundPoints: Int)
    {
        var newPoints: Int = 0
        var bonus: Int = 0
        var teamId:Int = 0
        var svId:Int = 0
        when (team)
        {
            1 -> {
                newPoints = roundPoints + tempBonus1 + team1Points
                team1Points = newPoints
                bonus = tempBonus1
                svId = R.id.sv1

                teamId = R.id.PointsLayout1
                val totalTv1: TextView = findViewById(R.id.TotalPoints1)
                totalTv1.text = newPoints.toString()

            }
            2 -> {
                newPoints = roundPoints + tempBonus2 + team2Points
                team2Points = newPoints
                bonus = tempBonus2
                svId = R.id.sv2

                teamId = R.id.PointsLayout2
                val totalTv2: TextView = findViewById(R.id.TotalPoints2)
                totalTv2.text = newPoints.toString()
            }
            else -> {
                helper.GiveError(this,10,"no such team?")
            }
        }
        val str:String = this.getString(R.string.PointsTemplate)
        val strfinished: String = String.format(str, bonus, roundPoints, round ,newPoints)
        val tv: TextView = TextView(this)
        tv.text = strfinished

        val ll:LinearLayout = findViewById(teamId)
        ll.addView(tv)

        val scrollview = (findViewById<View>(svId) as ScrollView)
        scrollview.post { scrollview.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun AddTempBonus(team:Int,tempBonus:Int,pos:Boolean) : Int
    {
        var newBonus = 0
        if(pos)
        {
            newBonus = tempBonus + 20
        }
        else
        {
            if (tempBonus != 0)
            {
                newBonus = tempBonus - 20
            }
        }
        var teamId:Int = 0
        when (team)
        {
            1 -> {
                teamId = R.id.CurrentBonus1
            }
            2 -> {
                teamId = R.id.CurrentBonus2
            }
            else -> {
                helper.GiveError(this,10,"no such team?")
            }
        }
        val tv:TextView = findViewById(teamId)
        tv.text = String.format(this.getString(R.string.currentBonus), newBonus)

        return newBonus
    }

    private fun EndRound(team:Int)
    {
        val dia:Dialog = Dialog(this)

        //show dialog
        dia.closeOptionsMenu()
        dia.setContentView(R.layout.dialog)



        val confirmButton:Button = dia.findViewById<Button>(R.id.DialogAdd)
        confirmButton.setOnClickListener()
        {
            val eText =  dia.findViewById<View>(R.id.editTextNumber) as EditText
            val strNumb:String = eText.text.toString()
            if (strNumb.isNotEmpty())
            {
                val numb:Int = strNumb.toInt()
                if (numb <= 162)
                {
                    AddPointsToTeam(team, numb)
                    if(team == 1)
                    {
                        AddPointsToTeam(2, 162-numb)
                    }
                    else if(team == 2)
                    {
                        AddPointsToTeam(1, 162-numb)
                    }
                    dia.hide()
                    round++
                    tempBonus1 = 0;
                    tempBonus2 = 0;
                    val tv1:TextView = findViewById(R.id.CurrentBonus1)
                    tv1.text = String.format(this.getString(R.string.currentBonus), tempBonus1)
                    val tv2:TextView = findViewById(R.id.CurrentBonus2)
                    tv2.text = String.format(this.getString(R.string.currentBonus), tempBonus2)
                }
            }
        }
        dia.show()
    }
}
