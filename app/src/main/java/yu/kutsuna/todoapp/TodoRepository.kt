package yu.kutsuna.todoapp

import yu.kutsuna.todoapp.data.Todo

class TodoRepository {
    fun getTodoList(): List<Todo> {
        return db.findAll()
    }

    fun getActiveTodoList(): List<Todo> {
        return db.findActive()
    }

    fun getCompletedTodoList(): List<Todo> {
        return db.findCompleted()
    }

    fun updateCompleted(todo: Todo) {
        db.updateCompleted(todo)
    }

    fun addTodo(todo: Todo) {
        db.addTodo(todo)
    }

    fun deleteTodo(id: String) {
        db.delete(id)
    }

    companion object {
        private val db = TodoApplication.database.todoDao()
    }
}