package org.rhasspy.mobile.android.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class MicrophoneWidgetReceiver: GlanceAppWidgetReceiver(){
    override val glanceAppWidget: GlanceAppWidget
        get() = MicrophoneWidget()
}