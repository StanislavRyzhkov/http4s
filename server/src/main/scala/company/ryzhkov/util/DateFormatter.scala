package company.ryzhkov.util

import java.text.SimpleDateFormat
import java.util.Date

trait DateFormatter {
  def dateToString(date: Date): String =
    new SimpleDateFormat("dd-MM-yyyy") format date
}
