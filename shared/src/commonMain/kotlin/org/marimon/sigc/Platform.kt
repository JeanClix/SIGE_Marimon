package org.marimon.sigc

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform