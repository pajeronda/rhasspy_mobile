object Version {

    const val major = 0
    const val minor = 4
    const val patch = 10
    const val code = 95

    override fun toString(): String {
        return "$major.$minor.$patch-$code"
    }

}