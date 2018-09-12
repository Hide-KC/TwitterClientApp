package com.kc.twitterclientapp

class ColorSubject: Subject<HSBView.IColorObserver, HSBView.HSB>() {
    override fun notify(hsb: HSBView.HSB) {
        for (observer in observers){
            observer.colorUpdate(hsb)
        }
    }
}