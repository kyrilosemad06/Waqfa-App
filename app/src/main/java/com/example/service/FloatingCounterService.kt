package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.ReelLimitApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FloatingCounterService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var serviceJob: Job? = null

    private lateinit var countTextView: TextView
    private lateinit var labelTextView: TextView
    private lateinit var rootContainer: LinearLayout

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, buildForegroundNotification())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
            initFloatingOverlay()
            observeCounterUpdates()
        }
    }

    private fun buildForegroundNotification(): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, ReelLimitApplication.CHANNEL_OVERLAY)
            .setContentTitle("WAQFA Active")
            .setContentText("Monitoring short-form video quota in real time")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun initFloatingOverlay() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 40
            y = 200
        }

        rootContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(28, 16, 28, 16)
            elevation = 16f
        }

        labelTextView = TextView(this).apply {
            text = "WAQFA "
            setTextColor(Color.parseColor("#9FB5C9"))
            textSize = 11f
            paint.isFakeBoldText = true
        }

        countTextView = TextView(this).apply {
            text = "0 / 20"
            setTextColor(Color.parseColor("#FFFFFF"))
            textSize = 13f
            paint.isFakeBoldText = true
        }

        rootContainer.addView(labelTextView)
        rootContainer.addView(countTextView)

        updatePillBackground(Color.parseColor("#0B1D2D"), Color.parseColor("#14D9E6"))

        // Drag to move handling
        rootContainer.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(rootContainer, params)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        // If tap with very small delta, open app
                        val dx = Math.abs(event.rawX - initialTouchX)
                        val dy = Math.abs(event.rawY - initialTouchY)
                        if (dx < 10 && dy < 10) {
                            val intent = Intent(this@FloatingCounterService, MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            startActivity(intent)
                        }
                        return true
                    }
                }
                return false
            }
        })

        try {
            windowManager?.addView(rootContainer, params)
            floatingView = rootContainer
        } catch (_: Exception) {}
    }

    private fun updatePillBackground(bgColor: Int, strokeColor: Int) {
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 60f
            setColor(bgColor)
            setStroke(3, strokeColor)
        }
        rootContainer.background = drawable
    }

    private fun observeCounterUpdates() {
        val app = application as? ReelLimitApplication ?: return
        serviceJob = CoroutineScope(Dispatchers.Main).launch {
            app.counterEngine.currentStats.collectLatest { stats ->
                val settings = app.settingsRepository.settingsFlow.value
                val total = stats.totalCount
                val limit = settings.dailyLimit

                countTextView.text = "$total / $limit"

                val isLimit = total >= limit
                val isWarning = total >= (limit * settings.warningThresholdPercent) / 100

                when {
                    isLimit -> {
                        updatePillBackground(Color.parseColor("#250E11"), Color.parseColor("#FF626C"))
                        countTextView.setTextColor(Color.parseColor("#FF626C"))
                    }
                    isWarning -> {
                        updatePillBackground(Color.parseColor("#261B0E"), Color.parseColor("#FFB454"))
                        countTextView.setTextColor(Color.parseColor("#FFB454"))
                    }
                    else -> {
                        updatePillBackground(Color.parseColor("#0B1D2D"), Color.parseColor("#14D9E6"))
                        countTextView.setTextColor(Color.parseColor("#14D9E6"))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        serviceJob?.cancel()
        floatingView?.let { view ->
            try {
                windowManager?.removeView(view)
            } catch (_: Exception) {}
        }
        floatingView = null
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, FloatingCounterService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, FloatingCounterService::class.java)
            context.stopService(intent)
        }
    }
}
