package com.example.klaverjassen

import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.google.android.material.floatingactionbutton.FloatingActionButton

val helper:Helper = Helper()

class MainActivity : ComponentActivity() {

    //set up variables
    private var teamPoints = Array<Int>(2) {0}
    private var tempBonus = Array<Int>(2) {0}
    private var editing = Array<Boolean>(2) {false}

    private var round:Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //Set up buttons
        val editButtons = arrayOf<FloatingActionButton>(findViewById<FloatingActionButton>(R.id.FloatEdit1), findViewById<FloatingActionButton>(R.id.FloatEdit2))
        var team:Int = 0
        for (butt in editButtons)
        {
            val localTeam = team
            butt.setOnClickListener()
            {
                if (editing[localTeam])
                {
                    editing[localTeam] = false
                    val po = arrayOf<EditText>(findViewById(R.id.pointsOverView1), findViewById(R.id.pointsOverView2))
                    po[localTeam].isFocusable = false
                    po[localTeam].isFocusableInTouchMode = false
                    po[localTeam].isClickable =true
                    po[localTeam].clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(po[localTeam].windowToken, 0)
                    butt.setImageResource(android.R.drawable.ic_menu_edit)

                    val strings = po[localTeam].text.split("\n")
                    if (strings.last().isNotEmpty())
                    {
                        teamPoints[localTeam] = strings.last().toInt()
                    }
                    val tp = arrayOf<TextView>(findViewById(R.id.TotalPoints1), findViewById(R.id.TotalPoints2))
                    tp[localTeam].text = teamPoints[localTeam].toString()

                }
                else
                {
                    editing[localTeam] = true
                    val po = arrayOf<EditText>(findViewById(R.id.pointsOverView1), findViewById(R.id.pointsOverView2))
                    po[localTeam].isFocusable = true
                    po[localTeam].isFocusableInTouchMode = true
                    po[localTeam].isClickable =false
                    po[localTeam].requestFocus()
                    butt.setImageResource(R.drawable.checkmark)
                }

            }
            team++
        }
        val bonusButtons = arrayOf<Button>(findViewById<Button>(R.id.Bonus1), findViewById<Button>(R.id.Bonus2))
        val remBonusButtons = arrayOf<Button>(findViewById<Button>(R.id.remBonus1), findViewById<Button>(R.id.remBonus2))
        team = 0
        while (team != 2)
        {
            val localTeam = team
            bonusButtons[localTeam].setOnClickListener()
            {
                AddTempBonus(localTeam, true)
            }
            remBonusButtons[localTeam].setOnClickListener()
            {
                AddTempBonus(localTeam,false)
            }
            team++
        }

        val addButtons = arrayOf(findViewById<Button>(R.id.Addpoints1),findViewById<Button>(R.id.Addpoints2))
        team = 0
        for(button in addButtons)
        {
            val localTeam = team
            button.setOnClickListener()
            {
                val goingButtons =
                    arrayOf<ToggleButton>(findViewById(R.id.Going1), findViewById(R.id.Going2))
                var isTeamGoing: Boolean = false
                for (gb in goingButtons) {
                    if (gb.isChecked) {
                        isTeamGoing = true
                        break
                    }
                }
                if (isTeamGoing)
                {
                    EndRound(localTeam)
                }
                else
                {
                    helper.ShowToast(this, this.getString(R.string.NoGoingTeam))
                }
            }
            team++
        }

        val goingButtons = arrayOf<ToggleButton>(findViewById(R.id.Going1),findViewById(R.id.Going2))
        team = 0
        for (but in goingButtons)
        {
            val localTeam = team
            but.setOnClickListener()
            {
                var tt = 0
                while (tt < 2)
                {
                    if (tt != localTeam)
                    {
                        if(goingButtons[tt].isChecked)
                        {
                            goingButtons[tt].isChecked = false
                        }
                    }
                    tt++
                }
            }
            team++
        }

        // set visual startup values
        val totalPointsTexts = arrayOf<TextView>(findViewById<TextView>( R.id.CurrentBonus1),findViewById<TextView>( R.id.CurrentBonus2))
        for(tv in totalPointsTexts)
        {
            tv.text = String.format(this.getString(R.string.currentBonus), 0)
        }
    }


    private fun AddPointsToTeam(team:Int, roundPoints: Int)
    {
        teamPoints[team] += roundPoints + tempBonus[team]

        val tp = arrayOf<TextView>(findViewById(R.id.TotalPoints1), findViewById(R.id.TotalPoints2))
        tp[team].text = teamPoints[team].toString()

        val str:String = this.getString(R.string.PointsTemplate)
        val strfinished: String = String.format(str, tempBonus[team], roundPoints, round ,teamPoints[team])
        val po = arrayOf<EditText>(findViewById(R.id.pointsOverView1), findViewById(R.id.pointsOverView2)) //linear layout in scroll view
        if (po[team].text.isNotEmpty())
        {
            po[team].text.append("\n")
        }
        po[team].text.append(strfinished)
        val sv = arrayOf<ScrollView>(findViewById(R.id.sv1), findViewById(R.id.sv2))
        sv[team].post { sv[team].fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun AddTempBonus(team:Int,pos:Boolean)
    {
        if(pos)
        {
            tempBonus[team] += 20
        }
        else
        {
            tempBonus[team] -= 20
        }
        val currentBonus = arrayOf(R.id.CurrentBonus1, R.id.CurrentBonus2)
        val tv:TextView = findViewById(currentBonus[team])
        tv.text = String.format(this.getString(R.string.currentBonus), tempBonus[team])
    }

    private fun EndRound(team:Int) {

        val maxPoints = 162
        val dia: Dialog = Dialog(this)

        //show dialog
        dia.closeOptionsMenu()
        dia.setContentView(R.layout.dialog)

        val confirmButton: Button = dia.findViewById<Button>(R.id.DialogAdd)
        confirmButton.setOnClickListener()
        {
            var contin: Boolean = false
            var numb: Int = 0

            val eText = dia.findViewById<View>(R.id.editTextNumber) as EditText
            val strNumb: String = eText.text.toString()
            if (strNumb.isNotEmpty()) {
                contin = true
                numb = strNumb.toInt()
            }
            var tt: Int = 0
            if (dia.findViewById<CheckBox>(R.id.Pit).isChecked) {
                contin = true
                numb = maxPoints
                tempBonus[team] += 100
                while (tt != 2) {
                    if (tt != team) {
                        tempBonus[tt] = 0
                    }
                    tt++
                }
            }
            if (dia.findViewById<CheckBox>(R.id.Cheated).isChecked)
            {
                contin = true
                while (tt != 2) {
                    if (tt != team) {
                        if (tempBonus[tt] >= 0) {
                            tempBonus[team] += tempBonus[tt]
                        }
                        tempBonus[tt] = 0
                        numb = maxPoints
                    }
                    tt++
                }
            }

            if (numb <= maxPoints && contin)
            {
                val filledPoints = Array<Int>(2) { 0 }
                val tempPoints = Array<Int>(2) { 0 }
                tempPoints[team] = numb + tempBonus[team]
                filledPoints[team] = numb

                while (tt != 2) {
                    if (tt != team) {
                        tempPoints[tt] = maxPoints - numb + tempBonus[tt]
                        filledPoints[tt] = maxPoints - numb
                    }
                    tt++
                }
                val goingButtons =
                    arrayOf<ToggleButton>(findViewById(R.id.Going1), findViewById(R.id.Going2))
                val totalPointsInRound: Int = tempPoints.sum()

                var goingTeam: Int = 0
                while (goingTeam < goingButtons.size) {
                    if (goingButtons[goingTeam].isChecked) {
                        break
                    }
                    goingTeam++
                }

                //all points go to opposite team
                if (tempPoints[goingTeam] < totalPointsInRound / 2) {
                    tt = 0
                    while (tt != 2) {
                        if (tt != goingTeam) {
                            tempBonus[tt] += tempBonus[goingTeam]
                            tempBonus[goingTeam] = 0
                            teamPoints[goingTeam] = 0
                            filledPoints[tt] = maxPoints
                            filledPoints[goingTeam] = 0
                        }
                        tt++
                    }
                }
                tt = 0
                while (tt != 2) {
                    AddPointsToTeam(tt, filledPoints[tt])
                    tt++
                }
                dia.hide()
                round++

                val tv = arrayOf<TextView>(
                    findViewById(R.id.CurrentBonus1),
                    findViewById(R.id.CurrentBonus2)
                )
                tt = 0
                while (tt < tempBonus.size) {
                    tempBonus[tt] = 0
                    tv[tt].text =
                        String.format(this.getString(R.string.currentBonus), tempBonus[tt])
                    tt++
                }
                for (butt in goingButtons) {
                    butt.isChecked = false
                }

            }
            else
            {
                helper.ShowToast(this, this.getString(R.string.NoOutcome))
            }
        }
        dia.show()
    }

    // credit to Zelyson on github https://gist.github.com/sc0rch/7c982999e5821e6338c25390f50d2993
    /**
     * Clear focus on touch outside for all EditText inputs.
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

}
