package com.safiislam.nexvault.core.crypto

class KeyUnavailableException(name: String) :
    IllegalStateException("Key '$name' cannot be unwrapped on this device")
