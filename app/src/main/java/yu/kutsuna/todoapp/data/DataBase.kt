package yu.kutsuna.todoapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TodoDao::class], version = 1)
abstract class DataBase: RoomDatabase() {
    abstract fun todoDao(): TodoDao
}