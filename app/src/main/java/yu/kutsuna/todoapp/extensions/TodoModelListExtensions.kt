package yu.kutsuna.todoapp.extensions

import yu.kutsuna.todoapp.data.TodoModel

/**
 * チェック済みアイテムの存在可否をチェックする
 */
fun List<TodoModel>.existCheckedItem(): Boolean {
    return this.any { it.isChecked }
}

/**
 * TodoModelリストのチェック状態をリセットする
 * return: チェック済みアイテムの存在可否（常にfalse）
 */
fun List<TodoModel>.resetChecked(): Boolean {
    this.forEach {
        if (!it.todo.isCompleted) {
            it.isChecked = false
        }
    }

    return false
}

/**
 * TodoModelリストのチェック状態を全てチェック済みにする
 * return: チェック済みアイテムの存在可否（常にtrue）
 */
fun List<TodoModel>.setAllChecked(): Boolean {
    this.forEach {
        if (!it.todo.isCompleted) {
            it.isChecked = true
        }
    }

    return true
}

/**
 * TodoModelリストのチェック済み状態をチェックする
 * 全てチェック済み：true
 * チェック済みでないアイテムが存在する：false
 */
fun List<TodoModel>.isAllChecked(): Boolean {
    return this.filter { it.isChecked }.size == this.size - this.filter { it.todo.isCompleted }.size
}
