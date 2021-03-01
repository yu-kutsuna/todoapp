package yu.kutsuna.todoapp.row

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import yu.kutsuna.todoapp.data.TodoModel
import yu.kutsuna.todoapp.extensions.getInversionCheckedItem

class TodoRowViewModel(private val todoModel: TodoModel, private val position: Int) : ViewModel() {
    val checkedPosition: MutableLiveData<Int> = MutableLiveData()
    val item: MutableLiveData<TodoModel> = MutableLiveData()

    fun init() {
        item.value = todoModel
    }

    /**
     * Todoのテキスト押下時の処理
     * チェックボックスのチェック状態を反転する
     */
    fun clickCheck(view: View) {
        item.value?.let {
            item.value = it.getInversionCheckedItem()
            checkedPosition.value = position
        }
    }

}