package colorpicker

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.SeekBar
import com.kc.twitterclientapp.R
import kotlinx.android.synthetic.main.dialog_colorpicker.view.*

class ColorPickerDialogFragment : DialogFragment() {
    companion object {
        fun newInstance(targetFragment: Fragment?): ColorPickerDialogFragment {
            val fragment = ColorPickerDialogFragment()
            fragment.setTargetFragment(targetFragment, 0)
            return fragment
        }
    }

    private val subject: Subject<IColorObserver, AHSB> = ColorSubject()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity?.layoutInflater
        if (inflater != null){
            val view = inflater.inflate(R.layout.dialog_colorpicker, null, false)
            subject.attach(view.sb_plane)
            val rValue = view.r_value.also {
                it.setObserver(object : IColorObserver {
                    override fun colorUpdate(ahsb: AHSB) {
                        val color = Color.HSVToColor(floatArrayOf(ahsb.hue, ahsb.saturation, ahsb.brightness))
                        it.text = "Red " + Color.red(color).toString()
                    }
                })
            }
            val gValue = view.g_value.also {
                it.setObserver(object : IColorObserver {
                    override fun colorUpdate(ahsb: AHSB) {
                        val color = Color.HSVToColor(floatArrayOf(ahsb.hue, ahsb.saturation, ahsb.brightness))
                        it.text = "Green " + Color.green(color).toString()
                    }
                })
            }
            val bValue = view.b_value.also {
                it.setObserver(object : IColorObserver {
                    override fun colorUpdate(ahsb: AHSB) {
                        val color  = Color.HSVToColor(floatArrayOf(ahsb.hue,ahsb.saturation,ahsb.brightness))
                        it.text = "Blue " + Color.blue(color).toString()
                    }
                })
            }

            subject.attach(rValue)
            subject.attach(gValue)
            subject.attach(bValue)

            val hueBar = view.hue_bar.also {
                it.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                    override fun onStartTrackingTouch(seekBar: SeekBar?) { }
                    override fun onStopTrackingTouch(seekBar: SeekBar?) { }
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        it.hue = it.getProgressAsFloat()
                        it.progressChanged()
                    }
                })
            }

            subject.attach(hueBar)

            val builder = AlertDialog.Builder(context)
            builder.setView(view)
                    .setPositiveButton(android.R.string.ok){ dialogInterface: DialogInterface, i: Int ->
                        dismiss()
                    }
            return builder.create()
        } else {
            val builder = AlertDialog.Builder(context)
            builder.setView(null)
            return builder.create()
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        subject.detachAll()
    }

    fun changed(ahsb: AHSB) {
        subject.notify(ahsb)
    }
}