package com.safiislam.nexvault.data.repository

import com.safiislam.nexvault.core.publish.PublishScheduler

internal class CountingScheduler : PublishScheduler {
    var kicks = 0
    override fun kick() {
        kicks++
    }
}
