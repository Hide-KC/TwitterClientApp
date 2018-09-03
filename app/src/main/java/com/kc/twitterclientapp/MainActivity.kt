package com.kc.twitterclientapp

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.Job

class MainActivity : AppCompatActivity(), ProgressDialogFragment.ICancel, TwitterTask.UpdateListener {
    private enum class FragmentTag{
        PARTICIPANTS, PROGRESS
    }
    private var task: TwitterTask? = null

    override fun update(count: Int) {
        val fragment = supportFragmentManager.findFragmentByTag(FragmentTag.PROGRESS.name)
        if (fragment is ProgressDialogFragment){
            fragment.setCount(count)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConfirmOAuthActivity.REQUEST_CODE && resultCode == Activity.RESULT_CANCELED){
            //CANCELだった場合、終了
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        task?.setRootJob()
    }

    override fun onPause() {
        super.onPause()
        task?.cancelAll()
    }

    override fun cancel(){
        task?.cancelAll()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (TwitterUtils.loadAccessToken(this) == null){
            val intent = Intent(this, ConfirmOAuthActivity::class.java)
            startActivityForResult(intent, ConfirmOAuthActivity.REQUEST_CODE)
        }

        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener {
            val id = it.itemId
            if (id == R.id.update_follows){
                task = TwitterTask(this) //臭うコード

                //キャンセル付きダイアログ表示
                val dialog = ProgressDialogFragment.newInstance()
                dialog.show(supportFragmentManager, FragmentTag.PROGRESS.name)

                //フォロー一覧を取得
                val participants = task?.getParticipants()
                if (participants != null){
                    val fragment = supportFragmentManager
                            .findFragmentByTag(FragmentTag.PARTICIPANTS.name)
                    if (fragment is ParticipantsFragment){
                        fragment.setAdapter(participants)
                    }
                }
            }
            return@setOnMenuItemClickListener true
        }

        //参加者リストFragmentのセット
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.participant_frame, ParticipantsFragment.newInstance(null))
        transaction.commit()
    }
}
