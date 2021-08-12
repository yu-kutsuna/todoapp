package yu.kutsuna.todoapp

import android.app.Application
import android.content.Context
import androidx.room.Room
import yu.kutsuna.todoapp.data.DataBase

class TodoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, objectOf<DataBase>(), "todo.db").build()
        pkgName = packageName
        context = applicationContext
    }

    private inline fun <reified T : Any> objectOf() = T::class.java

    companion object {
        lateinit var database: DataBase
        lateinit var pkgName: String
        lateinit var context: Context
    }
}