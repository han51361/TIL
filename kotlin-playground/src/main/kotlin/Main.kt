class Main {

    // bad code
    fun alphabet(): String {
        val result = StringBuilder()
        for (letter in 'A'..'Z') {
            result.append(letter)
        }

        result.append("\nresult : set")
        return result.toString()
    }

    fun main() {
        fun alphabetWith() = with(StringBuilder()) {
            // fact : with params : 1st - stringBuilder 2nd: lambda
            // 밑에 나오는 this == stringBuilder
            for (letter in 'A'..'Z') {
                this.append("\nresult : set")
            }

            this.toString()
        }

    }

    // apply vs with : apply - 본인에게 전달된 객체(수신 객체)를 반환한다.
    fun useApply() = StringBuilder().apply {
        for (letter in 'A'..'Z') {
            append(letter)
        }

        append("\nresult : set")
    }.toString()

}
