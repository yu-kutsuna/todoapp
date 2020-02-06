package yu.kutsuna.todoapp.data

import androidx.room.PrimaryKey

data class TodoModel(
    val todo: Todo,
    var isChecked: Boolean
)