package yu.kutsuna.todoapp

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import yu.kutsuna.todoapp.data.TodoModel

/**
 * 拡張関数ファイル
 */

/**
 * ソフトキーボードを非表示にする拡張関数
 */
fun Activity.hideKeyboard() {
    hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
}

fun Context.hideKeyboard(view: View?) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
}

/**
 * チェック済みアイテムの存在可否をチェックする
 */
fun List<TodoModel>.existCheckedItem(): Boolean {
    return this.any { it.isChecked }
}

/**
 * TodoModelリストのチェック状態をリセットする
 */
fun List<TodoModel>.resetChecked() {
    this.forEach {
        if (!it.todo.isCompleted) {
            it.isChecked = false
        }
    }
}

/**
 * TodoModelリストのチェック状態を全てチェック済みにする
 */
fun List<TodoModel>.setAllChecked() {
    this.forEach {
        if (!it.todo.isCompleted) {
            it.isChecked = true
        }
    }
}

/**
 * TodoModelリストのチェック済み状態をチェックする
 * 全てチェック済み：true
 * チェック済みでないアイテムが存在する：false
 */
fun List<TodoModel>.isAllChecked(): Boolean {
    return this.filter { it.isChecked }.size == this.size - this.filter { it.todo.isCompleted }.size
}

/**
 * TodoModelのチェック状態を反転する
 */
fun TodoModel.inversionChecked() {
    this.isChecked = !this.isChecked
}

/**
 * チェック状態を反転したTodoModelを取得する
 */
fun TodoModel.getInversionCheckedItem(): TodoModel {
    return TodoModel(this.todo, !this.isChecked)
}