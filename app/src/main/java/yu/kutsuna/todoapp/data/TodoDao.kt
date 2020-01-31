package yu.kutsuna.todoapp.data

import androidx.room.*

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTodo(value: String)

    @Query("SELECT * From Todo")
    fun findAll(): List<Todo>

    @Update
    fun updateCompleted(isCompleted: Boolean, id: Long)

    @Delete
    fun delete(id: Long)
}