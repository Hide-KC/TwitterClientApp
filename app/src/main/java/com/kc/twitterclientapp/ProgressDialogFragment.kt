package com.kc.twitterclientapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment

class ProgressDialogFragment: DialogFragment() {
    interface ICancel{
        fun cancel()
    }

    private var cancellable: ICancel? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ICancel){
            cancellable = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = "実行中..."

        val builder = AlertDialog.Builder(context)
        builder.setTitle("")
                .setNegativeButton("cancel") { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.cancel()
                }
                .setMessage(message)

        return builder.create()
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        cancellable?.cancel()
    }

    companion object {
        fun newInstance(): ProgressDialogFragment{
            val fragment = ProgressDialogFragment()
            return fragment
        }
    }
}