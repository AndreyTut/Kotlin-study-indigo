fun isNumber(input: String): Any {
    return try {
        val value = input.toInt()
        value
    } catch (e: NumberFormatException) {
        input
    }
}// write this function

