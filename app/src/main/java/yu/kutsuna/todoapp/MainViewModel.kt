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
    var isListExist: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var isEmptyAddText: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var isAllSelectClicked: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var isViewingDeleteDialog: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var isLoading: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var todoList: MutableLiveData<List<Todo>> = MutableLiveData()
    var itemCountText: MutableLiveData<String> = MutableLiveData()
    private lateinit var deleteId: String
    private var textValue: CharSequence? = null
    private val todoRepository = TodoRepository()
    fun init() {
        isEmptyAddText.value = true
        updateList()
    }

    fun addText(text: CharSequence?) {
        Log.d("test", "addText $text" )
        textValue = text
        isEmptyAddText.value = text.isNullOrEmpty()
    }

    fun clickAddButton(view: View) {
        GlobalScope.launch(Dispatchers.Main) {
            isLoading.value = true
            withContext(Dispatchers.Default) {
                todoRepository.addTodo(Todo(0, textValue.toString(), false))
            }
            isLoading.value = false

            updateList()
        }
    }

    private fun updateList() {
        GlobalScope.launch(Dispatchers.Main) {
            isLoading.value = true
            todoList.value  =
                withContext(Dispatchers.Default) {
                    todoRepository.getTodoList().reversed()
                }
            todoList.value?.let {
                if(it.isNullOrEmpty()) {
                    isListExist.value = false
                } else {
                    isListExist.value = true
                    itemCountText.value = "${it.size} items"
                }
            }
            isLoading.value = false
            Log.d("test", "isListExist ${isListExist.value}" )
        }
    }

    fun clickAllSelect(view: View) {
        isAllSelectClicked.value?.let {
            isAllSelectClicked.value = !it
        }
    }

    fun clickDeleteIcon(id: String) {
        deleteId = id
        isViewingDeleteDialog.value = true
    }

    fun clickDeleteDialogYes(view: View) {
        GlobalScope.launch(Dispatchers.Main) {
            isLoading.value = true
            withContext(Dispatchers.Default) {
                todoRepository.deleteTodo(deleteId)
            }
            isLoading.value = false
            updateList()
            isViewingDeleteDialog.value = false
        }
    }

    fun clickDeleteDialogNo(view: View) {
        isViewingDeleteDialog.value = false
    }
}