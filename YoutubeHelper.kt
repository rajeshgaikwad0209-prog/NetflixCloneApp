package com.example.netflixclone

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun launchYouTube(context: Context, url: String) {
    // Check if the URL is valid before trying to open it
    if (url.isNotEmpty()) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            // This flag ensures that if the app is opened from a different context, it doesn't crash
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open Video Player", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Trailer link not available", Toast.LENGTH_SHORT).show()
    }
}