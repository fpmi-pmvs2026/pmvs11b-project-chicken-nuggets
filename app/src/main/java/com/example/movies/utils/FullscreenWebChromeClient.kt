package com.example.movies.utils

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient

@Suppress("DEPRECATION")
open class FullscreenWebChromeClient(
    private val activity: Activity
) : WebChromeClient() {

    private var customView: View? = null
    private var customViewCallback: CustomViewCallback? = null

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        if (customView != null) {
            callback?.onCustomViewHidden()
            return
        }

        customView = view
        customViewCallback = callback

        activity.window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        activity.addContentView(
            view,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    override fun onHideCustomView() {
        customView?.visibility = View.GONE
        customViewCallback?.onCustomViewHidden()

        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

        customView = null
        customViewCallback = null
    }
}
