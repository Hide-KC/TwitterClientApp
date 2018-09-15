package colorpicker

interface IColorObserver{
    //Subject#notifyからの受信用
    fun colorUpdate(ahsb: AHSB)
}

interface ColorChangeListener{
    //変更発報用
    fun changed(ahsb: AHSB)
}