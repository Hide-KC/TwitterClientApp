package com.kc.twitterclientapp

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewParent

abstract class HSBView : View {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var mAlpha: Float = 0f
        set(value){
            field = value.coerceIn(0f..1f)
        }

    var hue: Float = 0f
        set(value){
            var degree = value
            if (degree > 360 || degree < 0){
                //360度以内に格納
                degree %= 360
                degree += 360
                degree %= 360
            }
            field = degree
        }

    var saturation: Float = 0f
        set(value){
            field = value.coerceIn(0f..1f)
        }

    var brightness: Float = 1f
        set(value){
            field = value.coerceIn(0f..1f)
        }
}