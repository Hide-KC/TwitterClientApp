package colorpicker

class ColorSubject: Subject<IColorObserver, AHSB>() {
    private var ahsb = AHSB()

    override fun notify(parameter: AHSB) {
        ahsb = parameter
        for (observer in observers){
            observer.colorUpdate(ahsb)
        }
    }
}