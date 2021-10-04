package io.properit.adveritywarehouse.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundException : RuntimeException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, ex: Exception?) : super(message, ex)
}