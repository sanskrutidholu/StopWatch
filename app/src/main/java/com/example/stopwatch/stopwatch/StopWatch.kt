package com.example.stopwatch.stopwatch

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.stopwatch.R

class StopWatch : AppCompatActivity() {

    lateinit var listView : ListView
    lateinit var lapbtn : Button
    lateinit var stopbtn : Button
    lateinit var timeView: TextView
    //lateinit var lapTimeViewer : TextView
    private var seconds : Int = 0
    private var minutes:Int = 0
    private var milliSeconds:Int = 0
    private var stopwatchOFFState = true
    private var stopWatchTime: Long = 0
    private var millisecondTime:Long = 0
    private var startTime : Long = 0
    var lapTime:Long = 0
    private var updateTime:Long = 0L
    var handler: Handler? = null
    private var listElementsArrayList: ArrayList<Lap>? = null
    var adapter: LapListViewAdapter?= null
    var lapCount = 0

    private var runnable : Runnable = object:Runnable {
        override fun run() {
            updateTime = SystemClock.elapsedRealtime() - startTime
            stopWatchTime = millisecondTime + updateTime
            timeView.text = getStringTime(stopWatchTime)
            //lapTimeViewer.text = getStringTime(updateTime + lapTime)
            handler!!.postDelayed(this, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_watch)

        lapbtn = findViewById(R.id.button1)
        stopbtn = findViewById(R.id.button2)
        timeView = findViewById(R.id.timeView)
        listView = findViewById(R.id.list1)
        //lapTimeViewer = findViewById(R.id.l)

        handler = Handler()
        listElementsArrayList = ArrayList<Lap>()
        adapter = LapListViewAdapter(this, R.layout.lap_time_list, listElementsArrayList!!)
        listView.adapter = adapter

        stopbtn.setOnClickListener {
            if (stopwatchOFFState) {
                startTime = SystemClock.elapsedRealtime()
                handler!!.postDelayed(runnable, 0)
                stopbtn.text = "Stop"
                lapbtn.text = "Lap"
                lapbtn.isEnabled = true
                stopwatchOFFState = false
            } else {
                lapTime += updateTime
                millisecondTime += updateTime
                handler!!.removeCallbacks(runnable)
                stopbtn.text = "Start"
                lapbtn.text = "Reset"
                stopwatchOFFState = true
            }
        }

        lapbtn.setOnClickListener {
            // if reset is clicked
            if (stopwatchOFFState) {
                stopWatchTime = 0L
                millisecondTime = 0L
                startTime = 0L
                updateTime = 0L
                lapTime = 0L
                lapbtn.text = "Lap"
                timeView.text = "00:00.00"
                //lapTimeViewer.text = "00:00.00"
                lapbtn.isEnabled = false
                lapCount = 0
                listElementsArrayList!!.clear()
                adapter!!.notifyDataSetChanged()
            }
            else {
                // if lapbtn is clicked
                val lapCountString  = "Lap" + ++lapCount
                val lap = Lap(lapCountString, getStringTime(updateTime + lapTime))
                lapTime = 0
                listElementsArrayList!!.add(0, lap)
                millisecondTime += updateTime
                startTime = SystemClock.elapsedRealtime()
                listView.smoothScrollToPosition(0)
                adapter!!.notifyDataSetChanged()
            }
        }
    }

   /* var runnable : Runnable = object : Runnable {
        override fun run () {
            UpdateTime = SystemClock.elapsedRealtime() - StartTime
            StopWatchTime = MillisecondTime + UpdateTime
            timeView.text = getStringTime(StopWatchTime)
            lapTimeViewer.text = getStringTime(UpdateTime + LapTime)
            handler!!.postDelayed(this, 0)

        }
    }*/

    fun getStringTime(Time: Long): String {
        seconds = (Time / 1000).toInt()
        minutes = seconds / 60
        seconds = seconds % 60
        milliSeconds = (Time % 1000).toInt()
        return ("" + String.format("%02d", minutes) + ":"
                + String.format("%02d", seconds) + "."
                + String.format("%02d", milliSeconds / 10))
    }
}


