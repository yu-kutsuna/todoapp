package yu.kutsuna.todoapp.extensions

import android.graphics.Paint
import android.widget.TextView

/**
 * TextViewに打ち消し線をつける
 */
fun TextView.setStrikeThrough() {
    this.paint.flags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    this.paint.isAntiAlias = true
}

/**
 * 打ち消し線を削除する
 */
fun TextView.clearPaint() {
    this.paint.flags = 0
}
