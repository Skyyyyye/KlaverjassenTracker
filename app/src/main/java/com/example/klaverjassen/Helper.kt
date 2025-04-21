package com.example.klaverjassen

import android.content.Context
import android.widget.Toast

class Helper {
    public fun GiveError(context: Context, errorNumb:Int, text:String)
    {
        val duration = Toast.LENGTH_SHORT

        val template:String = context.getString(R.string.Error)
        val str:String = String.format(template,errorNumb, text )
        val toast = Toast.makeText(context, str, duration) // in Activity
        toast.show()
    }
}

