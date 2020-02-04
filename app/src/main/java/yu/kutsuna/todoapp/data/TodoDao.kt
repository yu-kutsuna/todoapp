package yu.kutsuna.todoapp.data

import androidx.room.*

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTodo(todo: Todo)

    @Query("SELECT * From Todo")
    fun findAll(): List<Todo>

    @Query("SELECT * From Todo WHERE isCompleted = 0")
    fun findActive(): List<Todo>

    @Query("SELECT * From Todo WHERE isCompleted = 1")
    fun findCompleted(): List<Todo>

    @Query("UPDATE Todo SET isCompleted = 1, updateDate = :updateDate WHERE id = :id")
    fun updateCompleted(id: String, updateDate: String)

    @Query("DELETE FROM Todo WHERE id = :id")
    fun delete(id: String)
}