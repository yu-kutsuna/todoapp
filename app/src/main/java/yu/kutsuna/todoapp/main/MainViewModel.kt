package yu.kutsuna.todoapp.main

import android.content.Context
import android.view.View
import androidx.lifecycle.*
import kotlinx.coroutines.*
import yu.kutsuna.todoapp.R
import yu.kutsuna.todoapp.data.Todo
import yu.kutsuna.todoapp.data.TodoModel
import yu.kutsuna.todoapp.extensions.*
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(private val callback: Callback, private val context: Context) : ViewModel(),
    LifecycleObserver {

    /**
     * フッター選択状態を判別するためのEnum
     */
    enum class SelectedType {
        ALL, ACTIVE, COMPLETED
    }

    /**
     * Activityへのコールバック
     * Contextが必要な処理やAndroid独自のクラスを使う処理は
     * Activityで行う
     */
    interface Callback {
        fun finishAllClear()
    }

    class Factory constructor(private val callback: Callback, private val context: Context) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            MainViewModel(callback, context) as T
    }

    val selectedType: MutableLiveData<SelectedType> =
        MutableLiveData<SelectedType>().apply { value = SelectedType.ALL }
    val isListExist: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    val isCurrentTabListExist: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }
    val isCheckedAllSelect: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }
    val isEmptyAddText: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = true }
    val isViewingDeleteDialog: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }
    val isItemChecking: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }
    val isActiveItemChecking: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }
    val isLoading: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    val todoList: MutableLiveData<List<TodoModel>> = MutableLiveData()
    val itemCountText: MutableLiveData<String> = MutableLiveData()

    var checkedItemList: List<TodoModel> = listOf()
        set(value) {
            field = value
            isItemChecking.value = value.existCheckedItem()
            isActiveItemChecking.value = value.existCheckedActiveItem()
            todoList.value?.let {
                isCheckedAllSelect.value = it.isAllChecked()
            }
        }

    private var textValue: CharSequence? = null
    private val repository = MainRepository()

    /**
     * リストアップデート処理
     * 選択されたタイプに応じてRepositoryからTodoリストを取得する
     * ALLの場合のみ、フッター表示制御のためにLiveData:isListExitを更新する
     * リストが存在した場合はitemカウント数用のLiveDataも更新する
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun updateList() {
//        isItemChecking.value = todoList.value?.resetChecked()

        viewModelScope.launch {
            isLoading.value = true

            todoList.value =
                withContext(Dispatchers.Default) {
                    selectedType.value?.let { type ->
                        when (type) {
                            SelectedType.ALL -> getAllTodoList()
                            SelectedType.ACTIVE -> getAllTodoList().filter { item -> !item.todo.isCompleted }
                            SelectedType.COMPLETED -> getAllTodoList().filter { item -> item.todo.isCompleted }
                        }
                    }
                }?.apply {
                    if (selectedType.value == SelectedType.ALL) {
                        isListExist.value = this.isNotEmpty()
                    }
                    isCurrentTabListExist.value = this.isNotEmpty()
                    itemCountText.value = "${this.size} items"
                }?.map { todo ->
                    checkedItemList.forEach { checkedTodo ->
                        if (todo.todo.id == checkedTodo.todo.id) {
                            todo.isChecked = true
                        }
                    }
                    todo
                }

            isLoading.value = false
        }
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
                repository.addTodo(
                    Todo(
                        0, textValue.toString(), false,
                        context.getString(R.string.added, getNowDate())
                    )
                )
            }

            isCheckedAllSelect.value = false
            isLoading.value = false
            updateList()
        }
    }


    /**
     * 削除ボタン押下時の処理
     * LiveData：isViewingDeleteDialogを更新し、削除確認ダイアログを表示する
     */

    fun clickDeleteButton(view: View) {
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
                checkedItemList.forEach {
                    repository.deleteTodo(it.todo.id.toString())
                }
            }


            checkedItemList = listOf()
            isItemChecking.value = todoList.value?.resetChecked()
            isActiveItemChecking.value = false
            isCheckedAllSelect.value = false
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
     * クリアボタン押下時の処理
     * Repositoryから該当アイテムを完了済みにし、
     * リストを更新する
     */
    fun clickClear(view: View) {
        viewModelScope.launch(Dispatchers.Main) {
            isLoading.value = true
            var id = -1L
            withContext(Dispatchers.Default) {
                checkedItemList.forEach {
                    // 未完了のアイテムのみ処理する
                    if (!it.todo.isCompleted) {
                        repository.updateCompleted(
                            it.todo.id.toString(),
                            context.getString(R.string.completed_date, getNowDate())
                        )
                        id = it.todo.id
                    }
                }
            }
            checkedItemList = listOf()
            isItemChecking.value = todoList.value?.resetChecked()
            isActiveItemChecking.value = false
            isCheckedAllSelect.value = false
            isLoading.value = false
            updateList()
        }
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
     * 全選択ボタン押下時の処理
     * 未チェックアイテムが存在する場合は全てチェック済みに、
     * 全てのアイテムがチェック済みの場合は全て未チェックに変更する
     */
    fun clickAllSelect(view: View) {
        todoList.value?.let { todoList ->
            isItemChecking.value = if (todoList.isAllChecked()) {
                isCheckedAllSelect.value = false
                todoList.resetChecked()
            } else {
                isCheckedAllSelect.value = true
                todoList.setAllChecked()
            }
            checkedItemList = todoList.filter { it.isChecked }
            isActiveItemChecking.value = checkedItemList.existCheckedActiveItem()
        }

        callback.finishAllClear()
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