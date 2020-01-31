package yu.kutsuna.todoapp

import yu.kutsuna.todoapp.data.Todo

class TodoRepository {
    fun getTodoList(): List<Todo> {
        return TodoApplication.database.todoDao().findAll()
    }
}