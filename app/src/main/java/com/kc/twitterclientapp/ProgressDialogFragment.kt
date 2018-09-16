package com.kc.twitterclientapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_dialog.*
import kotlinx.android.synthetic.main.fragment_dialog.view.*

class ProgressDialogFragment: DialogFragment() {
    interface ICancel{
        fun cancel()
    }

    private var cancellable: ICancel? = null
    private lateinit var counter: TextView
    private lateinit var initCount: TextView

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ICancel){
            cancellable = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = "実行中..."
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.fragment_dialog, null, false)
        counter = view.counter
        initCount = view.init_count
        counter.text = "0"
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
                .setTitle(message)
                .setNegativeButton("cancel") { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.cancel()
                }

        return builder.create()
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        cancellable?.cancel()
        dialog?.dismiss()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        cancellable = null
    }

    fun setCount(count: Int){
        counter.text = count.toString()
    }

    fun setInit(count: Int){
        initCount.text = " / " + count.toString()
    }

    companion object {
        fun newInstance(): ProgressDialogFragment{
            return ProgressDialogFragment()
        }
    }
}