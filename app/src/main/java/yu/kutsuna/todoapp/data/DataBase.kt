package yu.kutsuna.todoapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Todo::class], version = 1)
abstract class DataBase: RoomDatabase() {
    abstract fun todoDao(): TodoDao
}