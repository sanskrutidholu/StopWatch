package com.example.stopwatch.timers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.stopwatch.R
import java.util.*

class TimersActivity : AppCompatActivity() {

    lateinit var etInputTime: EditText
    lateinit var tvCountDown: TextView
    lateinit var btnSet: Button
    lateinit var btnStart: Button
    lateinit var btnPause: Button
    lateinit var btnReset: Button

    private var Start_Time_In_Milli: Long = 600000
    private var EndTime: Long = 0
    var TimeLeftInMills: Long = Start_Time_In_Milli

    private lateinit var CountDownTimer: CountDownTimer
    var TimerRunning: Boolean = false

    private var NOTIFICATION_ID : Int= 0

    //  oncreate use to intilize all required views in our app
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timers)

        etInputTime = findViewById(R.id.edit_text_input)
        tvCountDown = findViewById(R.id.text_view_countdown)
        btnSet = findViewById(R.id.button_set)
        btnStart = findViewById(R.id.button_start)
        btnPause = findViewById(R.id.button_pause)
        btnReset = findViewById(R.id.button_reset)


        btnSet.setOnClickListener {
            val input = etInputTime.text.toString()
            if (input.isEmpty()) {
                Toast.makeText(this, "Please Enter Time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val millisInput = input.toLong() * 60000
            if (millisInput == 0L) {
                Toast.makeText(this, "Please enter a positive number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            setTime(millisInput)
            etInputTime.setText("")
        }

        btnStart.setOnClickListener {
            startTimer()
        }

        btnPause.setOnClickListener {
            pauseTimer()
        }

        btnReset.setOnClickListener {
            resetTimer()
        }
    }

    private fun pauseTimer() {
        CountDownTimer.cancel()
        TimerRunning = false
        // updateWatchInterface()
    }

    private fun startTimer() {
        EndTime = System.currentTimeMillis() + TimeLeftInMills
        CountDownTimer = object : CountDownTimer(TimeLeftInMills, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                TimeLeftInMills = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                TimerRunning = false
                // updateWatchInterface()
                getNotification()
            }
        }.start()

        TimerRunning = true
        // updateWatchInterface()
    }

    private fun resetTimer() {
        TimeLeftInMills = Start_Time_In_Milli
        updateCountDownText()
        // updateWatchInterface()
    }

    private fun updateCountDownText() {
        val hours: Int = ((TimeLeftInMills / 1000)/3600).toInt()
        val minutes: Int = ((TimeLeftInMills / 1000) / 60).toInt()
        val seconds: Int = ((TimeLeftInMills / 1000) % 60).toInt()
        val timeLeftFormatted: String = if (hours > 0) {
            String.format(
                Locale.getDefault(),
                "%d:%02d:%02d", hours, minutes, seconds
            )
        } else {
            java.lang.String.format(
                Locale.getDefault(),
                "%02d:%02d", minutes, seconds
            )
        }
        tvCountDown.text = timeLeftFormatted
    }

    /*private fun updateWatchInterface() {
        if (TimerRunning) {
            etInputTime.visibility = View.INVISIBLE
            btnSet.visibility = View.INVISIBLE
            btnReset.visibility = View.INVISIBLE
            btnStartPause.text = "Pause"
        }
        else {
            etInputTime.visibility = View.VISIBLE
            btnSet.visibility = View.VISIBLE
            btnStartPause.text = "Start"

            if (TimeLeftInMills < 1000) {
                btnStartPause.visibility = View.INVISIBLE
            } else {
                btnStartPause.visibility = View.VISIBLE
            }

            if (TimeLeftInMills < Start_Time_In_Milli) {
                btnReset.visibility = View.VISIBLE
            } else {
                btnReset.visibility = View.INVISIBLE
            }

        }
    }*/


    private fun setTime(millisInput: Long) {
        Start_Time_In_Milli = millisInput
        resetTimer()
    }


    //  this is use to save small amount of data when activity is running in background
    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong("millisLeft", TimeLeftInMills)
        outState.putBoolean("timerRunning", TimerRunning)
        outState.putLong("endTime", EndTime)

    }

    //  this is use to collect all the saved data by onSaveInstanceState method
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        TimeLeftInMills = savedInstanceState.getLong("millisLeft")
        TimerRunning = savedInstanceState.getBoolean("timerRunning")
        updateCountDownText()
        updateButtons()

        if (TimerRunning) {
            EndTime = savedInstanceState.getLong("endTime")
            TimeLeftInMills = EndTime -System.currentTimeMillis()
            startTimer()
        }
    }*/

    override fun onStop() {
        super.onStop()

        val prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()

        editor.putLong("millisLeft", TimeLeftInMills)
        editor.putBoolean("timerRunning", TimerRunning)
        editor.putLong("endTime", EndTime)
        editor.apply()

        if (CountDownTimer != null) {
            CountDownTimer.cancel()
        }

    }


    override fun onStart() {
        super.onStart()

        val prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        TimeLeftInMills = prefs.getLong("millisLeft", Start_Time_In_Milli)
        TimerRunning = prefs.getBoolean("timerRunning", false)

        updateCountDownText()
        // updateWatchInterface()

        if (TimerRunning) {
            EndTime = prefs.getLong("endTime", 0)
            TimeLeftInMills = EndTime - System.currentTimeMillis()

            if (TimeLeftInMills < 0) {

                TimeLeftInMills = 0
                TimerRunning = false
                updateCountDownText()

            } else {
                startTimer()
            }
        }
    }

    fun getNotification() {
        val builder : NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.timewatcher2)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.timewatcher2))
            .setContentTitle("TimeWatcher").setContentText("Your Target Has Achieved")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val path : Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        builder.setSound(path)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "YOUR_CHANNEL_ID"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel : NotificationChannel = NotificationChannel (
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(channelId)
        } else {
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }


    }
}


