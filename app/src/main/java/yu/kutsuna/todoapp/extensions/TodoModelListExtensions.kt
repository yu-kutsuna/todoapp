package yu.kutsuna.todoapp.extensions

import yu.kutsuna.todoapp.data.TodoModel

/**
 * チェック済みアイテムの存在可否をチェックする
 */
fun List<TodoModel>.existCheckedItem(): Boolean {
    return this.any { it.isChecked }
}

/**
 * チェック済みアクティブアイテムの存在可否をチェックする
 */
fun List<TodoModel>.existCheckedActiveItem(): Boolean {
    return this.any { it.isChecked && !it.todo.isCompleted }
}

/**
 * チェック済み完了アイテムの存在可否をチェックする
 */
fun List<TodoModel>.existCheckedCompletedItem(): Boolean {
    return this.any { it.isChecked && it.todo.isCompleted }
}

/**
 * TodoModelリストのチェック状態をリセットする
 * return: チェック済みアイテムの存在可否（常にfalse）
 */
fun List<TodoModel>.resetChecked(): Boolean {
    this.forEach {
        it.isChecked = false
    }

    return false
}

/**
 * TodoModelリストのチェック状態を全てチェック済みにする
 * return: チェック済みアイテムの存在可否（常にtrue）
 */
fun List<TodoModel>.setAllChecked(): Boolean {
    this.forEach {
        it.isChecked = true
    }

    return true
}

/**
 * TodoModelリストのチェック済み状態をチェックする
 * 全てチェック済み：true
 * チェック済みでないアイテムが存在する：false
 */
fun List<TodoModel>.isAllChecked(): Boolean {
    return this.filter { it.isChecked }.size == this.size
}
