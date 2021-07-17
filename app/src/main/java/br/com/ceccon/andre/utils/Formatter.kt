package br.com.ceccon.andre.utils

import java.text.SimpleDateFormat
import java.util.*

fun String.toDate(
    formatoDataFonte: String = "yyyy-MM-ddHH:mm:ss",
    formatoDesejado: String = "dd/MM/yyyy",
): String? {
    if (this.isEmpty() || this.isBlank()) return String()

    return SimpleDateFormat(
        formatoDataFonte, Locale.getDefault()
    ).parse(
        this.replace("T", "")
    )?.formatTo(formatoDesejado)
}

fun String?.getText(): String {
    if (this?.isBlank() == true || this?.isEmpty() == true) return "N/A"
    if (this?.isNotBlank() == true || this?.isNotEmpty() == true) return this
    return "N/A"
}

fun Date.formatTo(
    dateFormat: String = "ddMMyyyy-HHmmss",
): String {
    val formatter = SimpleDateFormat(
        dateFormat, Locale("pt", "BR")
    )

    return formatter.format(this)
}