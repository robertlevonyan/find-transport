package robert.findtransport.data.model.enums

enum class TransportType {
    MICROBUS_OLD,
    MICROBUS_NEW,
    BUS_BOGDAN,
    BUS_VIOLET,
    BUS_JONGTONG,
    BUS_MAN,
    TROLLEYBUS_OLD,
    TROLLEYBUS_NEW,
    METRO,
    BUS_HYUNDAI,
    BUS_PAZ,
    MICROBUS_SPRINTER,
    UNDEFINED;

    companion object {
        fun getByIndex(index: Int): TransportType =
            when (index) {
                0 -> MICROBUS_OLD
                1 -> MICROBUS_NEW
                2 -> BUS_BOGDAN
                3 -> BUS_VIOLET
                4 -> BUS_JONGTONG
                5 -> BUS_MAN
                6 -> TROLLEYBUS_OLD
                7 -> TROLLEYBUS_NEW
                8 -> METRO
                9 -> BUS_HYUNDAI
                10 -> BUS_PAZ
                11 -> MICROBUS_SPRINTER
                else -> UNDEFINED
            }
    }
}
