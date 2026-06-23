package com.painzone.ui.session

import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

// Rest overflow alert (M3.8): a short buzz + the device notification sound when the planned rest
// is exceeded. Platform side-effect, so it lives next to the screen and is triggered from a
// LaunchedEffect rather than the ViewModel (which holds no Context).
@Composable
fun rememberRestAlerter(): () -> Unit {
    val context = LocalContext.current
    return remember(context) { { fireRestAlert(context) } }
}

private fun fireRestAlert(context: Context) {
    vibrate(context)
    playNotificationSound(context)
}

private fun vibrate(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(VibratorManager::class.java)
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    } ?: return
    if (!vibrator.hasVibrator()) return
    vibrator.vibrate(VibrationEffect.createOneShot(BUZZ_MILLIS, VibrationEffect.DEFAULT_AMPLITUDE))
}

private fun playNotificationSound(context: Context) {
    val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) ?: return
    // Best-effort: a missing/unavailable ringtone shouldn't crash the session.
    runCatching { RingtoneManager.getRingtone(context, uri)?.play() }
}

private const val BUZZ_MILLIS = 400L
