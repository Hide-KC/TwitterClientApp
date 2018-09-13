package com.kc.twitterclientapp

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar

class HueBar(context: Context, attrs: AttributeSet?) : SeekBar(context, attrs), IColorObserver {

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
            field = when {
                value < 0f -> 0f
                value > 1f -> 1f
                else -> value
            }
        }

    var saturation: Float = 0f
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

    override fun colorUpdate(hsb: HSB) {
        progress = hsb.hue.toInt()
        hue = hsb.hue
        saturation = hsb.saturation
        brightness = hsb.brightness
        postInvalidateOnAnimation()
    }

    fun getFloatProgress(): Float {
        return super.getProgress() * 1f
    }
}