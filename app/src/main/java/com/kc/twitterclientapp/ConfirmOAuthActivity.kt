package com.kc.twitterclientapp

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class ConfirmOAuthActivity : AppCompatActivity() {
    private val oauth: TwitterOAuth = TwitterOAuth(this)
    companion object {
        val REQUEST_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_oauth)

        //即承認画面（Webページ）に飛ばす
        oauth.startAuthorize()
    }

    //Webページでアプリを承認するとここに戻ってくる
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        oauth.oAuthApproval(this, intent)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    //戻るボタンでActivityを閉じた場合はRESULT_CANCELE
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent()
        intent.putExtra("msg", "finish")
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }
}
