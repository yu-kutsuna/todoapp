package yu.kutsuna.todoapp

import yu.kutsuna.todoapp.data.Todo

class TodoRepository {
    fun getTodoList(): List<Todo> {
        return TodoApplication.database.todoDao().findAll()
    }

    fun addTodo(todo: Todo) {
        TodoApplication.database.todoDao().addTodo(todo)
    }

    fun deleteTodo(id: String) {
        TodoApplication.database.todoDao().delete(id)
    }
}