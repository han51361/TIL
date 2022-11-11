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
}
