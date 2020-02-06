package yu.kutsuna.todoapp.row

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TodoRowViewModel(val id: String): ViewModel() {
    var isCompleted: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var isChecked: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var deleteId: MutableLiveData<String> = MutableLiveData()
    var checkedId: MutableLiveData<String> = MutableLiveData()

    /**
     * 削除アイコン押下時の処理
     * MainViewModelに削除を通知する
     */
    fun clickDeleteIcon(view: View) {
        deleteId.value = id
    }

    /**
     * Todoのテキスト押下時の処理
     * 完了済：何もしない
     * 未完了：チェックボックスのチェック状態を反転する
     */
    fun clickCheck(view: View) {
        isChecked.value?.let {
            isChecked.value = !it
        }
        checkedId.value = id
    }

    fun checked(checked: Boolean) {
        isChecked.value = checked
    }
}