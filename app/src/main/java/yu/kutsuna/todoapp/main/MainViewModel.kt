package yu.kutsuna.todoapp.main

import android.view.View
import androidx.lifecycle.*
import kotlinx.coroutines.*
import yu.kutsuna.todoapp.data.Todo
import yu.kutsuna.todoapp.data.TodoModel
import yu.kutsuna.todoapp.isAllChecked
import yu.kutsuna.todoapp.resetChecked
import yu.kutsuna.todoapp.row.TodoViewAdapter
import yu.kutsuna.todoapp.setAllChecked
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel(), LifecycleObserver {

    /**
     * フッター選択状態を判別するためのEnum
     */
    enum class SelectedType {
        ALL, ACTIVE, COMPLETED
    }

    val selectedType: MutableLiveData<SelectedType> =
        MutableLiveData<SelectedType>().apply { value = SelectedType.ALL }
    val isListExist: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    val isEmptyAddText: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }
    val isViewingDeleteDialog: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }
    val isItemChecking: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }
    val isLoading: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    val todoList: MutableLiveData<List<TodoModel>> = MutableLiveData()
    val itemCountText: MutableLiveData<String> = MutableLiveData()

    private lateinit var deleteId: String
    lateinit var adapter: TodoViewAdapter

    private var textValue: CharSequence? = null
    private val repository = MainRepository()


    /**
     * 初期化処理
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun init() {
        isEmptyAddText.value = true
        updateList()
    }

    /**
     * EditTextの入力イベント検知時の処理
     * 入力されたテキストを保持し、
     * 空かどうかを判定するLiveDataを更新する(Addボタンの出しわけに使用)
     */
    fun addText(text: CharSequence?) {
        textValue = text
        isEmptyAddText.value = text.isNullOrEmpty()
    }

    /**
     * Addボタン押下時の処理
     * Repositoryからデータ追加を行い、
     * リストを更新する
     */
    fun clickAddButton(view: View) {
        viewModelScope.launch {
            isLoading.value = true
            withContext(Dispatchers.Default) {
                repository.addTodo(Todo(0, textValue.toString(), false, getAddedDate()))
            }
            isLoading.value = false

            updateList()
        }
    }

    /**
     * リストアップデート処理
     * 選択されたタイプに応じてRepositoryからTodoリストを取得する
     * ALLの場合のみ、フッター表示制御のためにLiveData:isListExitを更新する
     * リストが存在した場合はitemカウント数用のLiveDataも更新する
     */
    private fun updateList() {
        isItemChecking.value = todoList.value?.resetChecked()

        viewModelScope.launch {
            isLoading.value = true

            todoList.value =
                withContext(Dispatchers.Default) {
                    selectedType.value?.let {
                        when (it) {
                            SelectedType.ALL -> getAllTodoList()
                            SelectedType.ACTIVE -> getActiveTodoList()
                            SelectedType.COMPLETED -> {
                                getCompletedTodoList()
                            }
                        }
                    }
                }

            todoList.value?.let {
                if (selectedType.value == SelectedType.ALL) {
                    isListExist.value = !it.isNullOrEmpty()
                }
                itemCountText.value = "${it.size} items"
            }

            isLoading.value = false
        }
    }

    /**
     * 全選択ボタン押下時の処理
     */
    fun clickAllSelect(view: View) {
        todoList.value?.let { todoList ->
            isItemChecking.value = if (todoList.isAllChecked()) {
                todoList.resetChecked()
            } else {
                todoList.setAllChecked()
            }
        }
        adapter.notifyDataSetChanged()
    }

    /**
     * 削除アイコン押下時の処理
     * TodoViewAdapterから呼ばれる
     * LiveData：isViewingDeleteDialogを更新し、削除確認ダイアログを表示する
     */
    fun clickDeleteIcon(id: String) {
        deleteId = id
        isViewingDeleteDialog.value = true
    }

    /**
     * 削除確認ダイアログのYesボタン押下時の処理
     * チェック済みのアイテムが削除対象の場合はリストから除外
     * Repositoryから該当アイテム削除処理を実行し、
     * リストを更新する
     */
    fun clickDeleteDialogYes(view: View) {
        viewModelScope.launch {
            isLoading.value = true

            withContext(Dispatchers.Default) {
                repository.deleteTodo(deleteId)
            }
            isLoading.value = false
            updateList()
            isViewingDeleteDialog.value = false
        }
    }

    /**
     * 削除ダイアログのNoボタン押下時の処理
     * 削除ダイアログを非表示にする
     */
    fun clickDeleteDialogNo(view: View) {
        isViewingDeleteDialog.value = false
    }

    /**
     * フッター：All選択時の処理
     * selectedTypeをALLにし、
     * リストを更新する
     */
    fun clickAll(view: View) {
        selectedType.value = SelectedType.ALL
        updateList()
    }

    /**
     * フッター：Active選択時の処理
     * selectedTypeをActiveにし、
     * リストを更新する
     */
    fun clickActive(view: View) {
        selectedType.value = SelectedType.ACTIVE
        updateList()
    }

    /**
     * フッター：Completed選択時の処理
     * selectedTypeをCompletedにし、
     * リストを更新する
     */
    fun clickCompleted(view: View) {
        selectedType.value = SelectedType.COMPLETED
        updateList()
    }

    /**
     * クリアボタン押下時の処理
     * Repositoryから該当アイテムを完了済みにし、
     * リストを更新する
     */
    fun clickClear(view: View) {
        viewModelScope.launch(Dispatchers.Main) {
            isLoading.value = true
            withContext(Dispatchers.Default) {
                adapter.getCheckedList().forEach {
                    // 未完了のアイテムのみ処理する
                    if (!it.todo.isCompleted) {
                        repository.updateCompleted(it.todo.id.toString(), getCompletedDate())
                    }
                }
            }
            isLoading.value = false
            updateList()
        }
    }

    /**
     * 全てのTodoをリストで取得する
     */
    private fun getAllTodoList(): List<TodoModel> {
        val todoModelList = mutableListOf<TodoModel>()
        repository.getTodoList().reversed().forEach {
            todoModelList.add(TodoModel(it, false))
        }
        return todoModelList
    }

    /**
     * 未完了のTodoをリストで取得する
     */
    private fun getActiveTodoList(): List<TodoModel> {
        val todoModelList = mutableListOf<TodoModel>()
        repository.getActiveTodoList().reversed().forEach {
            todoModelList.add(TodoModel(it, false))
        }
        return todoModelList
    }

    /**
     * 完了済みのTodoをリストで取得する
     */
    private fun getCompletedTodoList(): List<TodoModel> {
        val todoModelList = mutableListOf<TodoModel>()
        repository.getCompletedTodoList().reversed().forEach {
            todoModelList.add(TodoModel(it, false))
        }
        return todoModelList
    }

    /**
     * 追加日時の取得
     */
    private fun getAddedDate(): String {
        return "Added: ${getNowDate()}"
    }

    /**
     * 完了日時の取得
     */
    private fun getCompletedDate(): String {
        return "Completed: ${getNowDate()}"
    }

    /**
     * 現在日時の取得
     */
    private fun getNowDate(): String {
        return SimpleDateFormat(
            "yyyy/MM/dd HH:mm",
            Locale.getDefault()
        ).format(Date(System.currentTimeMillis()))
    }

    @ExperimentalCoroutinesApi
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}