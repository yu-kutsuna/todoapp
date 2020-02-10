package yu.kutsuna.todoapp.row

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import yu.kutsuna.todoapp.data.TodoModel

class TodoRowViewModel(private val todoModel: TodoModel, private val position: Int) : ViewModel() {
    val deleteId: MutableLiveData<String> = MutableLiveData()
    val checkedPosition: MutableLiveData<Int> = MutableLiveData()
    val item: MutableLiveData<TodoModel> = MutableLiveData()

    fun init() {
        item.value = todoModel
    }

    /**
     * 削除アイコン押下時の処理
     * MainViewModelに削除を通知する
     */
    fun clickDeleteIcon(view: View) {
        deleteId.value = item.value?.todo?.id.toString()
    }

    /**
     * Todoのテキスト押下時の処理
     * チェックボックスのチェック状態を反転する
     */
    fun clickCheck(view: View) {
        item.value?.let {
            if (it.todo.isCompleted) return
            item.value = TodoModel(it.todo, !it.isChecked)
            checkedPosition.value = position
        }
    }

}