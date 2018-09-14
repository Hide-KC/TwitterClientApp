package com.kc.twitterclientapp

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import twitter4j.User

class MainActivity : AppCompatActivity(), ProgressDialogFragment.ICancel, TwitterTask.TwitterTaskListener, ColorChangeListener {
    override fun changed(ahsb: AHSB) {
        val fragment = supportFragmentManager.findFragmentByTag(FragmentTag.COLOR_PICKER.name)
        if (fragment is ColorPickerDialogFragment){
            fragment.changed(ahsb)
        }
    }

    override fun setInitCount(count: Int) {
        val fragment = supportFragmentManager.findFragmentByTag(FragmentTag.PROGRESS.name)
        if (fragment is ProgressDialogFragment){
            fragment.setInit(count)
        }
    }

    override fun complete(participants: List<User>?) {
        val fragment = supportFragmentManager
                .findFragmentByTag(FragmentTag.PARTICIPANTS.name)
        if (fragment is ParticipantsFragment && participants != null){
            fragment.setAdapter(participants)
        }

        val dialog = supportFragmentManager.findFragmentByTag(FragmentTag.PROGRESS.name)
        if (dialog is ProgressDialogFragment){
            dialog.dismiss()
        }
    }

    private enum class FragmentTag{
        PARTICIPANTS, PROGRESS, COLOR_PICKER
    }
    private lateinit var task: TwitterTask

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
        task.createRootJob()
    }

    override fun onPause() {
        super.onPause()
        task.cancelAll()
    }

    override fun cancel(){
        task.cancelAll()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (TwitterUtils.loadAccessToken(this) == null){
            val intent = Intent(this, ConfirmOAuthActivity::class.java)
            startActivityForResult(intent, ConfirmOAuthActivity.REQUEST_CODE)
        }

        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.title = getString(R.string.app_name)
        toolbar.setOnMenuItemClickListener { it ->
            val id = it.itemId
            when (id) {
                R.id.update_follows -> {
                    //キャンセル付きダイアログ表示
                    val dialog = ProgressDialogFragment.newInstance()
                    dialog.show(supportFragmentManager, FragmentTag.PROGRESS.name)

                    val fragment = supportFragmentManager.findFragmentByTag(FragmentTag.PARTICIPANTS.name)
                    if (fragment != null) {
                        (fragment as ParticipantsFragment).clear()
                    }

                    //フォロー一覧を取得
                    //コールバックでアダプターにセット
                    task.getParticipants()
                }
                R.id.show_colorpicker -> {
                    //カラーピッカーダイアログの表示
                    val dialog = ColorPickerDialogFragment.newInstance(null)
                    dialog.show(supportFragmentManager, FragmentTag.COLOR_PICKER.name)
                }
            }

            if (id == R.id.update_follows){
                //キャンセル付きダイアログ表示
                val dialog = ProgressDialogFragment.newInstance()
                dialog.show(supportFragmentManager, FragmentTag.PROGRESS.name)

                val fragment = supportFragmentManager.findFragmentByTag(FragmentTag.PARTICIPANTS.name)
                if (fragment != null){
                    (fragment as ParticipantsFragment).clear()
                }

                //フォロー一覧を取得
                //コールバックでアダプターにセット
                task.getParticipants()
            }
            return@setOnMenuItemClickListener true
        }

        //参加者リストFragmentのセット
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.participant_frame, ParticipantsFragment.newInstance(null), FragmentTag.PARTICIPANTS.name)
        transaction.commit()

        task = TwitterTask(this).also { it.createRootJob() }
    }
}
