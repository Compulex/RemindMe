package com.example.remindme

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.remindme.entities.AudioReminder
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class RecordingsAdapter : RecyclerView.Adapter<RecordingsAdapter.RecordingViewHolder>() {
    private var recList = mutableListOf<AudioReminder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder{
        return RecordingViewHolder.create(parent)
    }//onCreateViewHolder

    override fun getItemCount(): Int {
        return recList.size
    }//getItemCount

    override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
        holder.bind(recList, position)
    }//onBindViewHolder

    class RecordingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val title = itemView.findViewById<TextView>(R.id.rec_rem)
        private val ppBtn = itemView.findViewById<ImageButton>(R.id.play_pause)
        private val alarmTV = itemView.findViewById<TextView>(R.id.rec_time)

        @SuppressLint("SetTextI18n")
        fun bind(recList : MutableList<AudioReminder>, position: Int){
            val currRec = recList[position]
            title.text = currRec.title
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(currRec.audioFile)
            mediaPlayer.prepare()

            ppBtn?.setOnClickListener { playPause(mediaPlayer, ppBtn) }

            //display time
            /*if(currRec.onRepeat)
                alarmTV?.text = "Set for: Every ${currRec.amountTime} ${currRec.timeLength}"
            else*/

            if(currRec.amountTime == 0 && currRec.timeLength == ""){
                alarmTV?.text = "No alarm set"
            }
            else{
                alarmTV?.text = "Set for: ${currRec.amountTime} ${currRec.timeLength} from " +
                        "${currRec.date} ${currRec.time}"
            }

        }

        private fun playPause(mediaPlayer: MediaPlayer, ppBtn: ImageButton){
            //play
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.isLooping = false
            }

            if(!mediaPlayer.isPlaying) {
                mediaPlayer.start()
                Log.i("AUDIO PLAY", "Audio is currently playing")

                //currently playing change image to pause
                ppBtn.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, android.R.drawable.ic_media_pause))
            }
            else{
                mediaPlayer.pause()
                Log.i("AUDIO PAUSE", "User just paused audio")

                ppBtn.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, android.R.drawable.ic_media_play))
            }

            mediaPlayer.setOnCompletionListener {
                Log.i("AUDIO FINISHED", "Audio is finished playing")

                ppBtn.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, android.R.drawable.ic_media_play))
            }
        }//playPause

        companion object{
            fun create(parent: ViewGroup): RecordingViewHolder{
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recording_list_item, parent, false)
                return RecordingViewHolder(view)
            }
        }
    }//end inner class

    fun addReminder(recList: MutableList<AudioReminder>){
        this.recList = recList
        notifyDataSetChanged()
    }//addReminder

    fun getReminderAtPosition(position: Int): AudioReminder {
        return recList[position]
    }//getReminderAtPosition
}//end class - adapter