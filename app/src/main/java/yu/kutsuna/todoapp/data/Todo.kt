package yu.kutsuna.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Todo(
        @PrimaryKey(autoGenerate = true)
        val id: Long,
        val value: String,
        val isCompleted: Boolean,
        val updateDate: String
): Serializable