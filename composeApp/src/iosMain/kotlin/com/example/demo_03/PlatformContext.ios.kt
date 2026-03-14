package com.example.demo_03

import platform.Foundation.NSBundle

actual class PlatformContext(
    val bundle: NSBundle = NSBundle.mainBundle,
)
