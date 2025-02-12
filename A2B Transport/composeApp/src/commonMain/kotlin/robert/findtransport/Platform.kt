package robert.findtransport

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform