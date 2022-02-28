package com.example.remindme

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindme.database.ReminderDatabase
import com.example.remindme.dialogs.HelpDialog
import com.example.remindme.dialogs.UpdateDialog
import com.example.remindme.viewmodels.RecordingsListViewModel
import com.example.remindme.viewmodels.TextListViewModel
import java.io.File

class MainActivity : AppCompatActivity() {
    //recordings
    private lateinit var recVM : RecordingsListViewModel
    private lateinit var recAdapter : RecordingsAdapter
    private lateinit var listRecView : RecyclerView
    //texts
    private lateinit var txtVM : TextListViewModel
    private lateinit var txtAdapter: TextRemsAdapter
    private lateinit var listTxtView : RecyclerView
    //to pass into AlarmNotification
    private var notify = ""
    private var id = ""
    private var amtNum = 0
    private var timeLength = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Spinner
        val sort = findViewById<Spinner>(R.id.sort_by)
        //add list to spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.sort,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            //apply adapter to spinner
            sort.adapter = adapter
        }
        //order all recordings
        sort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val item = parent?.getItemAtPosition(pos)

                if(item == "A-Z"){
                    //sort recording adapter
                    recAdapter.sortAZ()

                    //sort texts adapter
                    txtAdapter.sortAZ()
                }
                else if(item == "Date Created"){
                    //sort recording adapter
                    recAdapter.sortDate()

                    //sort texts adapter
                    txtAdapter.sortDate()
                }

                listRecView.adapter = recAdapter
                listTxtView.adapter = txtAdapter
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                println("Nothing here")
            }
        }

        //initialize view models
        recAdapter = RecordingsAdapter()
        recVM = RecordingsListViewModel(this.application)
        recVM.getReminders().observe(this, { reminder-> recAdapter.addReminder(reminder) })

        txtAdapter = TextRemsAdapter()
        txtVM = TextListViewModel(this.application)
        txtVM.getReminders().observe(this, { reminder-> txtAdapter.addReminder(reminder) })

        //display list of recordings
        listRecView = findViewById(R.id.rec_list)
        listRecView.setHasFixedSize(true)
        listRecView.adapter = recAdapter

        //display list of text reminders
        listTxtView = findViewById(R.id.txt_list)
        listTxtView.setHasFixedSize(true)
        listTxtView.adapter = txtAdapter

        //set layout
        listRecView.layoutManager = LinearLayoutManager(this)
        listTxtView.layoutManager = LinearLayoutManager(this)

        //ADD
        val addBtn = findViewById<Button>(R.id.add_reminder)
        addBtn.setOnClickListener {
            val intent = Intent(this, AddReminder::class.java)
            startActivity(intent)
        }

        //view intent
        val viewIntent = Intent(this, ViewReminder::class.java)

        //DELETE - recordings
        val recDelete = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder): Boolean {
                    return false
                }//onMove

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.adapterPosition
                    val reminder = recAdapter.getReminderAtPosition(pos)

                    //delete media file
                    val directory = File(getExternalFilesDir(null)!!.absolutePath)
                    val files = directory.listFiles()
                    val idx = files!!.indexOf(File(reminder.audioFile))
                    val deleted = files[idx].deleteRecursively()
                    Log.i("Files Deleted?", deleted.toString())

                    recVM.deleteReminder(reminder)
                }//onSwiped
            }
        )

        //DELETE - texts
        val txtDelete = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {
                    return false
                }//onMove

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.adapterPosition
                    val reminder = txtAdapter.getReminderAtPosition(pos)

                    txtVM.deleteReminder(reminder)
                }//onSwiped
            }
        )

        //EDIT - recordings
        val recEdit = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder): Boolean {
                    return false
                }//onMove

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.adapterPosition
                    val reminder = recAdapter.getReminderAtPosition(pos)

                    //put extra in the intent
                    viewIntent.putExtra("view", "audio:${reminder.id}")
                    //go to view page
                    startActivity(viewIntent)
                }
            }
        )

        //EDIT - texts
        val txtEdit = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder): Boolean {
                    return false
                }//onMove

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.adapterPosition
                    val reminder = txtAdapter.getReminderAtPosition(pos)

                    //put extra in the intent
                    viewIntent.putExtra("view", "text:${reminder.id}")
                    //go to view page
                    startActivity(viewIntent)
                }
            }
        )

        //attach to recycler view
        recDelete.attachToRecyclerView(listRecView)
        recEdit.attachToRecyclerView(listRecView)

        txtDelete.attachToRecyclerView(listTxtView)
        txtEdit.attachToRecyclerView(listTxtView)

        //after reminder is added, set alarm notification
        val type = intent.getStringExtra("reminder_added")

        if(type != null){
            //get database
            val db = ReminderDatabase.getInstance(this.application)

            if(type == "audio"){
                //get list and data object
                val recordings = db.audioReminderDao().getAllRecReminders()
                val curReminder = recordings[recordings.size-1]

                //pass in to alarm notification
                notify = curReminder.title
                id = "$type:${curReminder.id}"
                amtNum = curReminder.amountTime
                timeLength = curReminder.timeLength
            }
            else if(type == "text"){
                //get list and data object
                val texts = db.textReminderDao().getAllTxtReminders()
                val curRem = texts[texts.size-1]

                //pass in to alarm notification
                notify = curRem.description
                id = "$type:${curRem.id}"
                amtNum = curRem.amountTime
                timeLength = curRem.timeLength
            }

            //set alarm for each notification
            val alarmNotification = AlarmNotification(this, notify, id, amtNum, timeLength)
            alarmNotification.createNotificationChannel()
            alarmNotification.scheduleNotification()
        }//intent from AddReminder
    }//onCreate

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }//onCreateOptionsMenu

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_help -> HelpDialog().show(supportFragmentManager, "help")
            R.id.menu_update -> UpdateDialog().show(supportFragmentManager, "update")
        }
        return super.onOptionsItemSelected(item)
    }//onOptionsItemSelected

}//end activity