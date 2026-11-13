package com.safiislam.nexvault.desktop.crypto

import com.safiislam.nexvault.core.crypto.CredentialCipher
import com.sun.jna.platform.win32.Crypt32Util

/** Tied to the signed-in OS user. */
class DpapiCredentialCipher : CredentialCipher {

    override fun encrypt(plaintext: ByteArray): ByteArray =
        Crypt32Util.cryptProtectData(plaintext)

    override fun decrypt(ciphertext: ByteArray): ByteArray =
        Crypt32Util.cryptUnprotectData(ciphertext)
}
