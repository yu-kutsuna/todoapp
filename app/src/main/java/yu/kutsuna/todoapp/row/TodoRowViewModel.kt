package yu.kutsuna.todoapp.row

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import yu.kutsuna.todoapp.data.TodoModel

class TodoRowViewModel(private val item: TodoModel): ViewModel() {
    val isCompleted: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = true }
    val isChecked: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply{value = false}
    val deleteId: MutableLiveData<String> = MutableLiveData()
    val checkedId: MutableLiveData<Long> = MutableLiveData()
    val value: MutableLiveData<String> = MutableLiveData()
    val date: MutableLiveData<String> = MutableLiveData()

    fun init() {
        value.value = item.todo.value
        date.value = item.todo.updateDate
        isChecked.value = item.isChecked
        isCompleted.value = item.todo.isCompleted
    }

    /**
     * 削除アイコン押下時の処理
     * MainViewModelに削除を通知する
     */
    fun clickDeleteIcon(view: View) {
        deleteId.value = item.todo.id.toString()
    }

    /**
     * Todoのテキスト押下時の処理
     * チェックボックスのチェック状態を反転する
     */
    fun clickCheck(view: View) {
        if(item.todo.isCompleted) return
        isChecked.value = isChecked.value?.let{ !it }
        checkedId.value = item.todo.id
    }

}