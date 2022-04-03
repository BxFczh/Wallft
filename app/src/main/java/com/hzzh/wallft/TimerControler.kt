package com.hzzh.wallft

import android.app.Activity
import android.content.Intent
import android.graphics.PointF
import android.icu.number.IntegerWidth
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import android.widget.SeekBar
import androidx.annotation.IntegerRes
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.load.engine.bitmap_recycle.IntegerArrayAdapter
import com.hzzh.wallft.databinding.ActivityTimerControlerBinding

class TimerControler : AppCompatActivity() {

    private lateinit var timerBinding: ActivityTimerControlerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timerBinding = ActivityTimerControlerBinding.inflate(layoutInflater)
        setContentView(timerBinding.root)
        overridePendingTransition(R.anim.anim_slide_in_bottom, R.anim.no_anim)

        val editText : EditText = findViewById(R.id.timerText)
        editText.filters = arrayOf(InputFilterMinMax(1, 60))

        var timer = intent.getLongExtra("timer", 60*1000)
        timerBinding.seekBar.setProgress(timer.toInt())
        timerBinding.timerText.hint = timer.toString()
        if (timer.toInt() == 1){
            timerBinding.textView.text = "min (1~60)"
        } else {
            timerBinding.textView.text = "mins (1~60)"
        }

        timerBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                timer = progress.toLong()
                timerBinding.timerText.hint = progress.toString()
                if (progress == 1){
                    timerBinding.textView.text = "min (1~60)"
                } else {
                    timerBinding.textView.text = "mins (1~60)"
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        timerBinding.timerText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    timerBinding.seekBar.progress = text.toString().toInt()
                } catch (e: Exception){}

            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        timerBinding.timerSet.setOnClickListener {
            val intent = Intent()
            intent.putExtra("return_timer", timer)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        if (MotionEvent.ACTION_OUTSIDE == event?.action) {
//            finish()
//            return true
//        }
        val x = event!!.getX()
        val y = event.getY()
        Log.d(x.toString(), y.toString())


        return super.onTouchEvent(event)
    }

}