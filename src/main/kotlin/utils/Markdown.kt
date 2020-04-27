package utils

class Markdown {
    companion object {
        fun escape(input: String): String {
            return input.replace("_", "\\_")
        }
    }
}