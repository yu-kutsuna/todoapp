package yu.kutsuna.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val value: String,
    val isCompleted: Boolean
)