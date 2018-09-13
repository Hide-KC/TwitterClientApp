package com.kc.twitterclientapp

interface IColorObserver{
    //Subject#notifyからの受信用
    fun colorUpdate(hsb: HSB)
}
interface ColorChangeListener{
    //変更発報用
    fun changed(hsb: HSB)
}