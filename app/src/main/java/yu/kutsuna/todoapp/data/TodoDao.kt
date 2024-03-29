package yu.kutsuna.todoapp.data

import androidx.room.*

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTodo(todo: Todo)

    @Query("SELECT * From Todo")
    fun findAll(): List<Todo>

    @Query("SELECT * From Todo Where isCompleted = 0")
    fun findActiveItem(): List<Todo>

    @Query("SELECT updateDate From Todo Where id = :id")
    fun findUpdateDate(id: String): String

    @Query("UPDATE Todo SET isCompleted = 1, updateDate = :updateDate WHERE id = :id")
    fun updateCompleted(id: String, updateDate: String)

    @Query("UPDATE Todo SET isCompleted = 0, updateDate = :updateDate WHERE id = :id")
    fun undoCompleted(id: String, updateDate: String)

    @Query("DELETE FROM Todo WHERE id = :id")
    fun delete(id: String)
}