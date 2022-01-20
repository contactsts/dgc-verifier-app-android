package ro.sts.dgc.certlogic

enum class TimeUnit {
    year, month, day, hour;

    companion object {
        fun isTimeUnitName(name: String?): Boolean {
            return try {
                valueOf(name!!)
                true
            } catch (iae: IllegalArgumentException) {
                false
            }
        }
    }
}