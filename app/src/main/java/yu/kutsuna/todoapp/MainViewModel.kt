package yu.kutsuna.todoapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import yu.kutsuna.todoapp.data.Todo

class MainViewModel: ViewModel() {
    var isListExist: MutableLiveData<Boolean> = MutableLiveData()
    var isEmptyAddText: MutableLiveData<Boolean> = MutableLiveData()
    var todoList: MutableLiveData<List<Todo>> = MutableLiveData()
    private val todoRepository = TodoRepository()

    fun init() {
        todoList.value = todoRepository.getTodoList().reversed()
        isListExist.value = !todoList.value.isNullOrEmpty()
    }
}