package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.ReelLimitApplication

/** A full-screen WAQFA barrier shown after the daily video limit is reached. */
class WaqfaBlockerService : Service() {
    private var windowManager: WindowManager? = null
    private var blocker: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, notification())
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) showBarrier()
    }

    private fun notification(): Notification {
        val open = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(this, ReelLimitApplication.CHANNEL_ALERTS)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle("WAQFA — Daily limit reached")
            .setContentText("Short-form video access is paused for today.")
            .setContentIntent(open)
            .setOngoing(true)
            .build()
    }

    private fun showBarrier() {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, type,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.CENTER }
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
            background = GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(Color.parseColor("#06121F"), Color.parseColor("#0B1D2D")))
            isClickable = true
        }
        val badge = TextView(this).apply {
            text = "W"
            textSize = 38f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#14D9E6"))
            background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(Color.parseColor("#112A3D")); setStroke(3, Color.parseColor("#14D9E6")) }
            layoutParams = LinearLayout.LayoutParams(112, 112).apply { bottomMargin = 28 }
        }
        val title = TextView(this).apply { text = "وقفة"; setTextColor(Color.WHITE); textSize = 30f; typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER }
        val body = TextView(this).apply { text = "أحسنت! التزمت بخطتك اليوم 👏\nوقتك أغلى من التمرير. جرّب واحدة من دول:"; setTextColor(Color.parseColor("#9FB5C9")); textSize = 17f; gravity = Gravity.CENTER; setLineSpacing(8f, 1f); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = 14; bottomMargin = 34 } }
        val openButton = TextView(this).apply {
            text = "فتح WAQFA"
            setTextColor(Color.parseColor("#06121F")); textSize = 16f; typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER
            setPadding(28, 20, 28, 20)
            background = GradientDrawable().apply { cornerRadius = 36f; setColor(Color.parseColor("#14D9E6")) }
            setOnClickListener {
                stop(this@WaqfaBlockerService)
                startActivity(Intent(this@WaqfaBlockerService, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP) })
            }
        }
        val alternatives = TextView(this).apply {
            text = "🧘 تنفّس 30 ثانية   •   📖 افتح كتاب   •   🚶 امشِ قليلًا"
            setTextColor(Color.parseColor("#14D9E6")); textSize = 14f; gravity = Gravity.CENTER
            setPadding(24, 18, 24, 18)
            background = GradientDrawable().apply { cornerRadius = 28f; setColor(Color.parseColor("#112A3D")) }
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 28 }
        }
        val homeButton = TextView(this).apply {
            text = "ارجع للشاشة الرئيسية"
            setTextColor(Color.parseColor("#9FB5C9")); textSize = 15f; gravity = Gravity.CENTER
            setPadding(28, 20, 28, 20)
            setOnClickListener {
                startActivity(Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME); addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
            }
        }
        root.addView(badge); root.addView(title); root.addView(body); root.addView(alternatives)
        root.addView(openButton, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        root.addView(homeButton, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        try { windowManager?.addView(root, params); blocker = root } catch (_: Exception) { stopSelf() }
    }

    override fun onDestroy() {
        blocker?.let { try { windowManager?.removeView(it) } catch (_: Exception) {} }
        blocker = null
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_ID = 1002
        fun start(context: Context) {
            val intent = Intent(context, WaqfaBlockerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(intent) else context.startService(intent)
        }
        fun stop(context: Context) = context.stopService(Intent(context, WaqfaBlockerService::class.java))
    }
}
