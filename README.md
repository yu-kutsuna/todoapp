# ToDoアプリ

## 基本構成
* アーキテクチャ → MVVM + DataBinding
* DB処理 → Room
* 非同期処理 → Coroutine
* DI → 今回は使っていません
* リスト表示 → RecyclerView

## DB構成
```
id Long: プライマリキー
value String: 入力したToDoのテキスト
isCompleted Boolean: 完了済み状態の判別に使うフラグ
updateDate String: 追加日時&完了日時の文字列

data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val value: String,
    val isCompleted: Boolean,
    val updateDate: String
)
```

## 機能
①EditTextにToDoを入力し、Addボタンを押下してリストに追加します
②表示中リストをフッターのボタンでAll（全部）、Active（未完了）、Completed（完了済み）のリストに変更できます
③リスト左のチェックボックスを押下するとフッターにClearボタンが表示されます。押下すると選択したアイテムが完了済みになります。
④リスト右の削除ボタンを押下すると削除ダイアログが表示されます。YESボタン押下でアイテムを個別に削除できます。
⑤EditText左の全選択アイコンを押下すると全てのアイテムを一括で選択できます。全てのアイテムが選択済みの場合は選択を一括解除します。
