package com.kc.twitterclientapp

class ColorSubject: Subject<IColorObserver, HSB>() {
    private var hsb = HSB(0f, 0f, 1f)

    override fun notify(parameter: HSB) {
        this.hsb = parameter
        for (observer in observers){
            observer.colorUpdate(this.hsb)
        }
    }
}