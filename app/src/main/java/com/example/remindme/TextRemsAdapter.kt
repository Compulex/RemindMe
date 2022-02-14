package com.example.remindme

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.remindme.entities.TextReminder
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class TextRemsAdapter : RecyclerView.Adapter<TextRemsAdapter.TextViewHolder>(){
    private var textList = mutableListOf<TextReminder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder.create(parent)
    }//onCreateViewHolder

    override fun getItemCount(): Int {
        return textList.size
    }//getItemCount

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        holder.bind(textList, position)
    }//onBindViewHolder

    class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val description = itemView.findViewById<TextView>(R.id.txt_rem)
        private val alarmTV = itemView.findViewById<TextView>(R.id.time_tv)

        @SuppressLint("SetTextI18n")
        fun bind(textList : MutableList<TextReminder>, position: Int){
            val currText = textList[position]
            description.text = currText.description

            if(currText.amountTime == 0 && currText.timeLength == ""){
                alarmTV.text = "No alarm set"
            }
            else{
                alarmTV.text = "Set for: ${currText.amountTime} ${currText.timeLength} from " +
                        "${currText.date} ${currText.time}"
            }
        }

        companion object{
            fun create(parent: ViewGroup): TextViewHolder{
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.text_list_item, parent, false)
                return TextViewHolder(view)
            }
        }
    }//end inner class

    fun addReminder(textList: MutableList<TextReminder>){
        this.textList = textList
        notifyDataSetChanged()
    }//addReminder

    fun getReminderAtPosition(position: Int): TextReminder{
        return textList[position]
    }//getReminderAtPosition

    fun sortAZ(){
        textList.sortWith { x, y -> x.description.compareTo(y.description) }
    }//sortAZ

    @RequiresApi(Build.VERSION_CODES.O)
    fun sortDate(){
        textList.sortWith(compareBy (
            { LocalDate.parse(it.date, DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) },
            { LocalTime.parse(it.time, DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)) }
        ))
    }//sortDate
}//end class - adapter
