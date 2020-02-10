package yu.kutsuna.todoapp.row

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import yu.kutsuna.todoapp.data.TodoModel

class TodoRowViewModel(val todo: TodoModel): ViewModel() {
    val isCompleted: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = true }
    val isChecked: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply{value = false}
    val deleteId: MutableLiveData<String> = MutableLiveData()
    val checkedId: MutableLiveData<Long> = MutableLiveData()
    val value: MutableLiveData<String> = MutableLiveData()
    val date: MutableLiveData<String> = MutableLiveData()
    var id: Long = 0

    fun init() {
        value.value = todo.todo.value
        date.value = todo.todo.updateDate
        isChecked.value = todo.isChecked
        id = todo.todo.id
        isCompleted.value = todo.todo.isCompleted
    }

    /**
     * 削除アイコン押下時の処理
     * MainViewModelに削除を通知する
     */
    fun clickDeleteIcon(view: View) {
        deleteId.value = id.toString()
    }

    /**
     * Todoのテキスト押下時の処理
     * チェックボックスのチェック状態を反転する
     */
    fun clickCheck(view: View) {
        if(todo.todo.isCompleted) return
        isChecked.value?.let {
            isChecked.value = !it
        }
        checkedId.value = id
    }

}