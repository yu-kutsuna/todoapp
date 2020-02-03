package yu.kutsuna.todoapp.data

import androidx.room.*

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTodo(todo: Todo)

    @Query("SELECT * From Todo")
    fun findAll(): List<Todo>

    @Update
    fun updateCompleted(todo: Todo)

    @Query("DELETE FROM Todo WHERE id = :id")
    fun delete(id: String)
}