package com.example.stopwatch.stopwatch


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.stopwatch.R


class LapListViewAdapter(context: Context, layoutResourceId: Int, data: ArrayList<Lap>) :
    ArrayAdapter<Lap>(context, layoutResourceId, data) {
    var data = ArrayList<Lap>()
    internal var context : Context
    var layoutResourceId:Int = 0

    init{
        this.layoutResourceId = layoutResourceId
        this.context = context
        this.data = data
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        var holder : LapHolder? = null

        val inflater  = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.lap_time_list, parent, false)
        holder = LapHolder()
        holder.lapNumber = view.findViewById(R.id.lap_number)
        holder.lapTime = view.findViewById(R.id.lap_time)

        val lap = data[position]
        holder.lapTime.text  = lap.lapTime
        holder.lapNumber.text = lap.numberOfLap
        return view
    }

    internal class LapHolder {
        internal lateinit var lapNumber: TextView
        internal lateinit var lapTime: TextView
    }

}

