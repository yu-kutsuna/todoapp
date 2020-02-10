package yu.kutsuna.todoapp.extensions

import yu.kutsuna.todoapp.data.TodoModel


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

