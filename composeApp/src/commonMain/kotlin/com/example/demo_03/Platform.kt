package com.example.demo_03

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform