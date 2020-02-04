package yu.kutsuna.todoapp

import android.app.Application
import androidx.room.Room
import yu.kutsuna.todoapp.data.DataBase

class TodoApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, objectOf<DataBase>(), "todo.db").build()
    }

    private inline fun <reified T : Any> objectOf() = T::class.java

    companion object {
        lateinit var database: DataBase
    }
}