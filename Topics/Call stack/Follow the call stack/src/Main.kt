val radius = 700

fun printIfPrime(number: Int) {
    var flag = true
    for (i in 2..number / 2) {
        if (number % i == 0) {
            flag = false
            break
        }
    }
    println(
        if (flag) "$number is a prime number."
        else "$number is not a prime number."
    )
}

fun main(args: Array<String>) {
    val number = readln().toInt()
    printIfPrime(number)
    var count = 100
    if (count > 99) {
        println(count++)
        val count = 120
        println(count)
    }
    println(count)
    println(radius)
    val radius = 800
    println(radius)
}