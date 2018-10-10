package fr.tp1.harkame.tp1.activity

class Controller internal constructor(private val mView: MainActivity) {

    fun itemSelected(notificationStyleSelected: String) {
        mView.itemSelected(notificationStyleSelected)
    }
}