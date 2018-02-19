package dayone.com.lrm.kotlinday1.code

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton
import dayone.com.lrm.kotlinday1.R
import dayone.com.lrm.kotlinday1.models.TimeSlot
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_time_difference.view.*

class MainActivity : Activity() {

    var millisecondTime:Long = 0L
    var startTime:Long = 0L
    var timeBuff:Long = 0L
    var updateTime = 0L

    var seconds:Int=0
    var minutes:Int=0;
    var milliseconds: Int=0

    var millisecondTimeDif:Long = 0L
    var startTimeDif:Long = 0L
    var secondsDif:Int=0
    var minutesDif:Int=0;
    var millisecondsDif: Int=0


    lateinit var handler: Handler;
    lateinit var thread: Thread
    lateinit var context: MainActivity
    var count :Int=0

    var mins: String=""
    var sec: String=""
    var mSec: String=""

    //crating an arraylist to store users using the data class user
    lateinit var timeSlotsList:ArrayList<TimeSlot>
    lateinit var adapter: Adapter<MyAndroidAdapter.ViewHolder>
    var isStop : Boolean= false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        timeSlotsList = ArrayList<TimeSlot>()
        initRecyclerView()
        handler= Handler()
        thread = Thread()
        //context= this@MainActivity();
        startTimer()
        checkChangeListeners()
        //creating our adapter

    }


        fun checkChangeListeners()
        {
            tb_start_resume.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener
            {
                override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                    if(p1)
                    {
                        handler.removeCallbacks(runnable);
                        isStop=true
                        tb_diff.text="RESET"

                    }
                    else
                    {
                        timeBuff += millisecondTime;
                        startTimer()
                        isStop=false
                        tb_diff.text="LAP"
                    }

                }

            })

            tb_diff.setOnClickListener {
                if(isStop)
                {
                    tb_diff.text="LAP"
                    handler.removeCallbacks(runnable,null)

                        millisecondTime = 0L ;
                        startTime = 0L ;
                        timeBuff = 0L ;
                        updateTime = 0L ;
                        seconds = 0 ;
                        minutes = 0 ;
                        milliseconds = 0 ;
                        tv_time_clock.text ="00 00 00"
                        tv_time_diff.text ="00 00 00"
                        timeSlotsList.clear();
                        count=0
                        adapter.notifyDataSetChanged()
                        isStop=false

                }
                else
                {
                   if(!tv_time_clock.text.equals("00 00 00")) {
                       //timeBuff += millisecondTime;
                       count = count + 1
                       timeSlotsList.add(TimeSlot(" $count", tv_time_diff.text.toString()))
                       adapter.notifyDataSetChanged()
                       minutesDif = 0
                       millisecondsDif = 0
                       startTimeDif=0
                       secondsDif = 0
                       millisecondTimeDif = 0
                       tv_time_diff.text ="00 00 00"
                       updateDiffText()
                       startTimeDif = SystemClock.uptimeMillis();
                       handler.postDelayed(runnable, 0);
                   }
                    isStop = false
                }
            }

        }


        //method to start the timer inside the activity
        fun startTimer()
        {
            startTime = SystemClock.uptimeMillis();
            startTimeDif = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
        }




        //to create the separate thread
        var runnable = Runnable()
        {
                startTime()
        }


        //to set the required values from system clock
        fun startTime()
        {
            millisecondTime = SystemClock.uptimeMillis()-startTime
            updateDiffText()
            updateTimerText()
            handler.postDelayed(runnable,0)
        }

        fun updateDiffText()
        {
            millisecondTimeDif=SystemClock.uptimeMillis()-startTimeDif
            secondsDif= (millisecondTimeDif/1000).toInt()
            minutesDif=secondsDif/60
            secondsDif= secondsDif%60
            millisecondsDif =(millisecondTimeDif%1000).toInt()

            mins= java.lang.String.format("%02d",minutesDif)
            sec= java.lang.String.format("%02d",secondsDif)
            mSec= java.lang.String.format("%02d",millisecondsDif)
            tv_time_diff.text= "$mins  $sec  $mSec"

        }

        fun updateTimerText()
        {
            updateTime = timeBuff+millisecondTime
            seconds= (updateTime/1000).toInt()
            minutes=seconds/60
            seconds= seconds%60
            milliseconds =(updateTime%1000).toInt()

            mins= java.lang.String.format("%02d",minutes)
            sec= java.lang.String.format("%02d",seconds)
            mSec= java.lang.String.format("%02d",milliseconds)
            tv_time_clock.text= "$mins  $sec  $mSec"
        }


        fun initRecyclerView()
        {
            val recyclerView = findViewById(R.id.rv_time_difference) as RecyclerView
            //adding a layoutmanager
            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false) as RecyclerView.LayoutManager?

            adapter = MyAndroidAdapter(timeSlotsList)
            //now adding the adapter to recyclerview
            recyclerView.adapter = adapter
        }

        class MyAndroidAdapter(val timeSlotList: ArrayList<TimeSlot>) : Adapter<MyAndroidAdapter.ViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAndroidAdapter.ViewHolder {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_time_difference, parent, false)
                return ViewHolder(v)
            }

            override fun onBindViewHolder(holder: MyAndroidAdapter.ViewHolder, position: Int) {
                holder.bindItems(timeSlotList[position])
            }

            override fun getItemCount(): Int {
                return timeSlotList.size
            }

            class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

                fun bindItems(timeSlot: TimeSlot)
                {
                    val tvSlotNo = itemView.findViewById(R.id.tv_slot_no) as TextView
                    val tvSlotDiff  = itemView.findViewById(R.id.tv_slot_time_diff) as TextView
                    tvSlotNo.text = timeSlot.slotName
                    tvSlotDiff.text = timeSlot.slotDifference

                }
            }
        }
}


