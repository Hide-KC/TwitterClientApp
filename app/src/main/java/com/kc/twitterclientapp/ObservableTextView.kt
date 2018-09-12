package com.kc.twitterclientapp

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

class ObservableTextView: TextView, HSBView.IColorObserver {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var observer: HSBView.IColorObserver? = null
    fun setObserver(observer: HSBView.IColorObserver){
        this.observer = observer
    }

    override fun colorUpdate(hsb: HSBView.HSB) {
        observer?.colorUpdate(hsb)
    }
}