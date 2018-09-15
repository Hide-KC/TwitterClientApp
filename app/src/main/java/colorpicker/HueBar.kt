package colorpicker

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar

class HueBar(context: Context, attrs: AttributeSet) : SeekBar(context, attrs), IColorObserver {
    var mAlpha: Float = 0f
        set(value){ field = value.coerceIn(0f..1f) }

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
        set(value){ field = value.coerceIn(0f..1f) }

    var brightness: Float = 1f
        set(value){ field = value.coerceIn(0f..1f) }

    fun progressChanged(){
        val ahsb = AHSB(mAlpha, hue, saturation, brightness)
        if (context is ColorChangeListener){
            val listener = context as ColorChangeListener
            listener.changed(ahsb)
        }
    }

    override fun colorUpdate(ahsb: AHSB) {
        mAlpha = ahsb.mAlpha
        progress = ahsb.hue.toInt()
        hue = ahsb.hue
        saturation = ahsb.saturation
        brightness = ahsb.brightness
        postInvalidateOnAnimation()
    }

    fun getFloatProgress(): Float {
        return super.getProgress() * 1f
    }
}