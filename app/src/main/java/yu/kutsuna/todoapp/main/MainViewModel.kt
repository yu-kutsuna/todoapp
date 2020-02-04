package yu.kutsuna.todoapp.main

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
    private val repository = MainRepository()
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
                repository.addTodo(Todo(0, textValue.toString(), false))
            }
            isLoading.value = false

            updateList()
        }
    }

    private fun updateList() {
        isItemChecking.value = false
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

            todoList.value?.let {
                if(selectedType == SelectedType.ALL) {
                    isListExist.value = !it.isNullOrEmpty()
                }
                itemCountText.value = "${it.size} items"
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

            // チェック済みアイテムが削除対象の場合はチェック済みアイテムリストから除外する
            run loop@{
                checkedIdList.forEachIndexed { index, id ->
                    if (id == deleteId) {
                        checkedIdList.removeAt(index)
                        return@loop
                    }
                }
            }

            withContext(Dispatchers.Default) {
                repository.deleteTodo(deleteId)
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
        selectedType =
            SelectedType.COMPLETED
        updateList()
    }

    fun clickClear(view: View) {
        GlobalScope.launch(Dispatchers.Main) {
            isLoading.value = true
            withContext(Dispatchers.Default) {
                checkedIdList.forEach {
                    repository.updateCompleted(it)
                }
            }
            isLoading.value = false
            updateList()
        }
    }

    private fun getAllTodoList(): List<Todo> {
        return repository.getTodoList().reversed()
    }

    private fun getActiveTodoList(): List<Todo> {
        return repository.getActiveTodoList().reversed()
    }

    private fun getCompletedTodoList(): List<Todo> {
        return repository.getCompletedTodoList().reversed()
    }
}