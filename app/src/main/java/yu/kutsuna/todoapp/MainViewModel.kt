package yu.kutsuna.todoapp

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yu.kutsuna.todoapp.data.Todo

class MainViewModel: ViewModel() {
    var isListExist: MutableLiveData<Boolean> = MutableLiveData()
    var isEmptyAddText: MutableLiveData<Boolean> = MutableLiveData()
    var isAllSelectClicked: MutableLiveData<Boolean> = MutableLiveData()
    var todoList: MutableLiveData<List<Todo>> = MutableLiveData()
    var itemCountText: MutableLiveData<String> = MutableLiveData()
    private var textValue: CharSequence? = null
    private val todoRepository = TodoRepository()

    fun init() {
        isEmptyAddText.value = true
        isAllSelectClicked.value = false
        updateList()
    }

    fun addText(text: CharSequence?) {
        Log.d("test", "addText $text" )
        textValue = text
        isEmptyAddText.value = text.isNullOrEmpty()
    }

    fun clickAddButton(view: View) {
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.Default) {
                todoRepository.addTodo(Todo(0, textValue.toString(), false))
            }
            updateList()
        }
    }

    private fun updateList() {
        GlobalScope.launch(Dispatchers.Main) {
            todoList.value  =
                withContext(Dispatchers.Default) {
                    todoRepository.getTodoList().reversed()
                }
            todoList.value?.let{
                for(todo in it) {
                    Log.d("test", "todo [${todo.id}]: ${todo.value}" )
                }
            }
            todoList.value?.let {
                if(it.isNullOrEmpty()) {
                    isListExist.value = false
                } else {
                    isListExist.value = true
                    itemCountText.value = "${it.size} items"
                }
            }
            Log.d("test", "isListExist ${isListExist.value}" )
        }
    }

    fun clickAllSelect(view: View) {
        isAllSelectClicked.value?.let {
            isAllSelectClicked.value = !it
        }
    }
}