package colorpicker

abstract class Subject<T, E> {
    protected val observers = mutableListOf<T>()
    abstract fun notify(parameter: E)

    fun attach(observer: T){
        observers.add(observer)
    }

    fun detachAll(){
        observers.clear()
    }

    fun detach(observer: T): Boolean{
        for(i in observers.indices){
            //参照等価
            if (observers[i] == observer){
                observers.removeAt(i)
                return true
            }
        }
        return false
    }

    private var state: States = States.WAIT
    enum class States{
        WAIT, RUNNING, ERROR
    }

    protected fun getState(): States {
        return state
    }

    protected fun setState(state: States) {
        this.state = state
    }
}