package yu.kutsuna.todoapp.main

import yu.kutsuna.todoapp.TodoApplication
import yu.kutsuna.todoapp.data.Todo

class MainRepository {

    fun getTodoList(): List<Todo> {
        return db.findAll()
    }

    fun getActiveTodoList(): List<Todo> {
        return db.findActive()
    }

    fun getCompletedTodoList(): List<Todo> {
        return db.findCompleted()
    }

    fun updateCompleted(id: String, date: String) {
        db.updateCompleted(id, date)
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