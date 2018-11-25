import java.text.SimpleDateFormat
import java.util.*

private val dayOfWeekFormatter = SimpleDateFormat("EEEE", Locale.ENGLISH)
private val asDayMonthYearFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)

fun attempt(action: () -> Unit, errorHandler: (Throwable) -> Unit = { it.printStackTrace() }) {
    try {
        action()
    } catch (error: Throwable) {
        errorHandler(error)
    }
}

fun Date.toDayOfWeek(): String {
    return dayOfWeekFormatter.format(this)
}

fun Date.asDayMonthYear(): String {
    return asDayMonthYearFormatter.format(this)
}