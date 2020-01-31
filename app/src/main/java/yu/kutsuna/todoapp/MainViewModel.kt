package yu.kutsuna.todoapp

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import yu.kutsuna.todoapp.data.Todo

class MainViewModel: ViewModel() {
    var isListExist: MutableLiveData<Boolean> = MutableLiveData()
    var isEmptyAddText: MutableLiveData<Boolean> = MutableLiveData()
    var todoList: MutableLiveData<List<Todo>> = MutableLiveData()
    private var textValue: CharSequence? = null
    private val todoRepository = TodoRepository()

    fun init() {
        updateList()
    }

    fun addText(text: CharSequence?) {
        textValue = text
        isEmptyAddText.value = text.isNullOrEmpty()
    }

    fun clickAddButton(view: View) {
        todoRepository.addTodo(Todo(0, textValue.toString(), false))
        updateList()
    }

    private fun updateList() {
        todoList.value = todoRepository.getTodoList().reversed()
        isListExist.value = !todoList.value.isNullOrEmpty()
    }
}