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
    enum class SelectedType {
        ALL, ACTIVE, COMPLETED
    }

    var selectedType = SelectedType.ALL
    var isListExist: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var isEmptyAddText: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var isAllSelectClicked: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var isViewingDeleteDialog: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var isItemChecking: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var isLoading: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var todoList: MutableLiveData<List<Todo>> = MutableLiveData()
    var itemCountText: MutableLiveData<String> = MutableLiveData()
    var checkedIdList: MutableList<String> = mutableListOf()
    private lateinit var deleteId: String
    private var textValue: CharSequence? = null
    private val todoRepository = TodoRepository()
    fun init() {
        isEmptyAddText.value = true
        updateList()
    }

    fun addText(text: CharSequence?) {
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
                    when(selectedType) {
                        SelectedType.ALL -> getAllTodoList()
                        SelectedType.ACTIVE -> getActiveTodoList()
                        SelectedType.COMPLETED -> getCompletedTodoList()
                    }
                }

            if(selectedType == SelectedType.ALL) {
                todoList.value?.let {
                    if (it.isNullOrEmpty()) {
                        isListExist.value = false
                    } else {
                        isListExist.value = true
                        itemCountText.value = "${it.size} items"
                    }
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

    fun clickAll(view: View) {
        selectedType = SelectedType.ALL
        updateList()
    }

    fun clickActive(view: View) {
        selectedType = SelectedType.ACTIVE
        updateList()
    }

    fun clickCompleted(view: View) {
        selectedType = SelectedType.COMPLETED
        updateList()
    }

    private fun getAllTodoList(): List<Todo> {
        return todoRepository.getTodoList().reversed()
    }

    private fun getActiveTodoList(): List<Todo> {
        return todoRepository.getActiveTodoList().reversed()
    }

    private fun getCompletedTodoList(): List<Todo> {
        return todoRepository.getCompletedTodoList().reversed()
    }
}