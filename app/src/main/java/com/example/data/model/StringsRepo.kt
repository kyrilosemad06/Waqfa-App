package com.example.data.model

object StringsRepo {

    fun get(key: String, lang: String): String {
        val isAr = lang == "ar"
        return when (key) {
            "app_name" -> "WAQFA"
            "tagline" -> if (isAr) "استرجع سيطرتك على وقتك." else "Take Back Your Attention."
            
            // Navigation
            "nav_dashboard" -> if (isAr) "الرئيسية" else "Dashboard"
            "nav_floating" -> if (isAr) "العداد العائم" else "Floating Pill"
            "nav_stats" -> if (isAr) "الإحصائيات" else "Statistics"
            "nav_apps" -> if (isAr) "التطبيقات" else "App Engines"
            "nav_settings" -> if (isAr) "الإعدادات" else "Settings"
            "nav_architecture" -> if (isAr) "الهندسة التقنية" else "Tech Plan"

            // Dashboard
            "quota_status" -> if (isAr) "رصيد الفيديوهات اليومي" else "Daily Video Quota"
            "videos_consumed" -> if (isAr) "فيديو تم مشاهدته" else "videos consumed"
            "remaining_quota" -> if (isAr) "متبقي في رصيدك" else "videos remaining"
            "quota_used" -> if (isAr) "من الرصيد مستخدم" else "of today's quota used"
            "limit_reached_badge" -> if (isAr) "تم استهلاك الرصيد بالكامل" else "Limit Reached"
            "warning_badge" -> if (isAr) "اقتربت من الحد اليومي" else "Approaching Limit"
            "normal_badge" -> if (isAr) "ضمن النطاق الصحي" else "Healthy Pace"
            "status_active" -> if (isAr) "الرصد قيد التشغيل" else "Live Monitoring Active"
            "status_demo" -> if (isAr) "وضع المحاكاة التفاعلي" else "Demo Simulator Mode"
            "status_paused" -> if (isAr) "الرصد متوقف مؤقتاً" else "Monitoring Paused"
            "status_permission" -> if (isAr) "بانتظار الصلاحيات" else "Permissions Needed"
            "strict_mode_active" -> if (isAr) "الوضع الصارم مفعّل" else "Strict Mode Active"
            "strict_mode_off" -> if (isAr) "الوضع الصارم معطّل" else "Strict Mode Disabled"
            "quick_edit_limit" -> if (isAr) "تعديل الحد" else "Edit Limit"
            "view_stats" -> if (isAr) "عرض الإحصائيات" else "View Stats"
            "enter_focus_mode" -> if (isAr) "تفعيل التركيز" else "Trigger Focus"
            "manual_correction" -> if (isAr) "تصحيح يدوي" else "Manual Correction"
            "add_manual_video" -> if (isAr) "+1 فيديو" else "+1 Video"
            "remove_manual_video" -> if (isAr) "-1 تصحيح" else "-1 Correct"
            "reset_today_confirm" -> if (isAr) "تصفير عداد اليوم" else "Reset Today"
            
            // Floating pill
            "floating_title" -> if (isAr) "معاينة العداد العائم" else "Floating Counter Overlay"
            "floating_desc" -> if (isAr) "شريط عائم صغير يظهر فوق التطبيقات أثناء التصفح لتذكيرك الدائم باستهلاكك." else "A minimal floating pill that hovers over supported apps to keep your usage visible in real time."
            "floating_preview_label" -> if (isAr) "معاينة تفاعلية (قابلة للسحب)" else "Interactive Preview (Draggable)"
            "floating_minimized" -> if (isAr) "الوضع المصغر" else "Compact Dot Mode"
            "floating_expanded" -> if (isAr) "الوضع الموسع" else "Pill Bar Mode"
            "simulate_video_view" -> if (isAr) "محاكاة مشاهدة فيديو (+1)" else "Simulate View Event (+1)"
            "test_warning_state" -> if (isAr) "محاكاة حالة التحذير" else "Test Warning State"
            "test_limit_reached" -> if (isAr) "محاكاة تجاوز الحد" else "Test Limit Reached"
            "native_overlay_perm" -> if (isAr) "صلاحية الظهور فوق التطبيقات" else "System Overlay Permission"
            "grant_overlay_perm" -> if (isAr) "منح صلاحية الظهور" else "Open Overlay Settings"
            "overlay_perm_granted" -> if (isAr) "ممنوحة ومفعّلة" else "Permission Granted"
            "overlay_perm_missing" -> if (isAr) "مطلوبة للظهور فوق إنستغرام وتيك توك" else "Required to float over reels"

            // Strict mode screen
            "limit_reached_title" -> if (isAr) "خلصت رصيدك النهارده." else "Daily Limit Reached."
            "limit_reached_subtitle" -> if (isAr) "خد استراحة وارجع لحاجاتك المهمة." else "Step back, take a breath, and return to what truly matters."
            "time_until_reset" -> if (isAr) "الوقت المتبقي حتى تجديد الرصيد" else "Time Until Next Reset"
            "return_home" -> if (isAr) "العودة للرئيسية" else "Return to Dashboard"
            "exception_button" -> if (isAr) "طلب استثناء طارئ (3 فيديوهات)" else "Request Intentional Exception (+3)"
            "exception_countdown" -> if (isAr) "انتظر للتفكير الواعي:" else "Deliberate Friction Delay:"
            "exception_used_all" -> if (isAr) "استنفدت جميع الاستثناءات لليوم" else "No exceptions left today"
            "native_enforcement_note" -> if (isAr) "تنويه تقني: يُظهر هذا العارض شاشة التوقف الوقائية لتصفية الذهن." else "Technical Note: In native Android, this launches a full-screen wellbeing overlay on target apps."

            // Statistics
            "stats_title" -> if (isAr) "تحليلات الانتباه" else "Attention Analytics"
            "weekly_consumption" -> if (isAr) "استهلاك آخر 7 أيام" else "7-Day Video Consumption"
            "adherence_streak" -> if (isAr) "أيام الالتزام بالحد" else "Limit Adherence Streak"
            "average_daily" -> if (isAr) "متوسط الاستهلاك اليومي" else "Daily Average"
            "app_breakdown_title" -> if (isAr) "توزيع المشاهدات حسب التطبيق" else "Distribution by Platform"
            "confirmed_vs_estimated" -> if (isAr) "المؤكد مقابل التقديري" else "Confirmed vs Heuristic"
            "clear_all_data" -> if (isAr) "مسح جميع البيانات المخزنة" else "Delete All Local Data"
            "clear_data_confirm" -> if (isAr) "هل أنت متأكد من مسح جميع البيانات والإحصائيات نهائياً؟" else "Are you sure you want to permanently delete all stats and history?"
            "cancel" -> if (isAr) "إلغاء" else "Cancel"
            "confirm" -> if (isAr) "تأكيد" else "Confirm"

            // App Management
            "app_engines_title" -> if (isAr) "محركات الرصد حسب التطبيق" else "App Detection Engines"
            "technical_honesty_banner" -> if (isAr) "شفافية تقنية كاملة: لا يدّعي التطبيق معرفة كل فيديو بنسبة 100% لأن واجهات المنصات تتغير باستمرار. نستخدم خوارزمية ذكية تجمع بين تغيرات الشاشة وزمن المشاهدة لمنع التكرار." else "Full Technical Honesty: Third-party apps cannot guarantee 100% video detection because platform UIs obfuscate internal views. WAQFA uses a multi-tier heuristic engine with debounce and dwell-time validation."
            "accessibility_service_status" -> if (isAr) "خدمة إمكانية الوصول (Accessibility)" else "Accessibility Service"
            "accessibility_service_btn" -> if (isAr) "فتح إعدادات إمكانية الوصول" else "Open Accessibility Settings"
            "confidence_label" -> if (isAr) "مستوى دقة الرصد:" else "Detection Confidence:"
            "enabled_switch" -> if (isAr) "تفعيل الرصد" else "Enable Limiting"
            "status_active_badge" -> if (isAr) "نشط" else "Active"
            "status_disabled_badge" -> if (isAr) "معطّل" else "Disabled"

            // Settings
            "settings_title" -> if (isAr) "إعدادات الرصيد والحدود" else "Settings & Quotas"
            "daily_limit_slider" -> if (isAr) "الحد الأقصى اليومي للفيديوهات" else "Daily Short-Video Quota"
            "warning_threshold_title" -> if (isAr) "نسبة التنبيه المسبق" else "Warning Threshold"
            "reset_time_title" -> if (isAr) "وقت التجديد اليومي" else "Daily Reset Time"
            "strict_mode_title" -> if (isAr) "الوضع الصارم عند استهلاك الرصيد" else "Strict Mode on Expiry"
            "strict_mode_desc" -> if (isAr) "يمنع الاستمرار وينتقل لشاشة استراحة التوقف الذهني فور نفاد الرصيد." else "Activates mindful pause screen immediately when your quota hits 0."
            "exceptions_title" -> if (isAr) "السماح باستثناء واعٍ واحد (+3 فيديوهات)" else "Allow Deliberate Exception (+3)"
            "exceptions_desc" -> if (isAr) "يتطلب الانتظار 10 ثوانٍ لمنع الاندفاع التلقائي والتحقق من النية." else "Requires a 10-second reflection timer before granting emergency quota."
            "demo_mode_toggle" -> if (isAr) "تفعيل وضع المحاكاة التفاعلية (للاختبار)" else "Interactive Demo Simulation Mode"
            "language_title" -> if (isAr) "اللغة / Language" else "Language / اللغة"

            // Onboarding
            "onboarding_welcome_title" -> if (isAr) "استرجع سيطرتك على وقتك" else "Take Back Your Attention"
            "onboarding_welcome_desc" -> if (isAr) "الفيديوهات القصيرة مصممة لسلب انتباهك بلا توقف. يمنحك WAQFA رصيداً يومياً واضحاً وعداداً عائماً يوقفك في اللحظة المناسبة." else "Short-form video feeds are engineered for mindless scrolling. WAQFA gives you a conscious daily quota, a transparent floating pill, and strict pause interventions."
            "onboarding_step_apps" -> if (isAr) "اختر المنصات المستهدفة" else "Target Platforms"
            "onboarding_step_quota" -> if (isAr) "حدد رصيدك اليومي" else "Set Your Daily Quota"
            "onboarding_step_transparency" -> if (isAr) "الشفافية والأذونات" else "Permissions & Transparency"
            "onboarding_get_started" -> if (isAr) "بدء الاستخدام" else "Get Started"
            "onboarding_next" -> if (isAr) "التالي" else "Next"
            "onboarding_back" -> if (isAr) "السابق" else "Back"

            else -> key
        }
    }
}
