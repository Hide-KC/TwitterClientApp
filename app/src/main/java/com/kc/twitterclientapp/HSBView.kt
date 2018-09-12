package com.kc.twitterclientapp

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewParent

abstract class HSBView : View {
    interface IColorObserver{
        //Subject#notifyからの受信用
        fun colorUpdate(hsb: HSB)
    }
    interface ColorChangeListener{
        //変更発報用
        fun changed(hsb: HSB)
    }

    data class HSB(val hue: Float, val saturation: Float, val brightness: Float)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var mAlpha: Float = 0f
        set(value){
            field = when {
                value < 0f -> 0f
                value > 1f -> 1f
                else -> value
            }
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

    var saturation: Float = 1f
        set(value){
            field = when {
                value < 0f -> 0f
                value > 1f -> 1f
                else -> value
            }
        }

    var brightness: Float = 1f
        set(value){
            field = when {
                value < 0f -> 0f
                value > 1f -> 1f
                else -> value
            }
        }
}