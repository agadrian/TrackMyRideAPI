package com.es.trackmyrideapi.exceptions


class BadRequestException(message: String) : RuntimeException("$DESCRIPTION $message"){
    companion object {
        const val DESCRIPTION = "Bad request exception (400)."
    }
}


class NotFoundException(message: String) : RuntimeException("$DESCRIPTION $message"){
    companion object {
        const val DESCRIPTION = "Not Found Exception (404)."
    }
}


class ConflictException(message: String) : RuntimeException("$DESCRIPTION $message"){
    companion object {
        const val DESCRIPTION = "Conflict Exception (409)."
    }
}

class AlreadyExistsException(message: String) : RuntimeException("$DESCRIPTION $message"){
    companion object {
        const val DESCRIPTION = "Conflict Exception (409)."
    }
}

class UnauthorizedException(message: String) : RuntimeException("$DESCRIPTION $message"){
    companion object {
        const val DESCRIPTION = "Unauthorized Exception (401)."
    }
}


class ForbiddenException(message: String) : RuntimeException("$DESCRIPTION $message"){
    companion object {
        const val DESCRIPTION = "Forbidden Exception (403)."
    }
}

class GeneralAppException(message: String) : RuntimeException("$DESCRIPTION $message") {
    companion object {
        const val DESCRIPTION = "Application Exception (500)."
    }
}

class FirebaseException(message: String) : RuntimeException("$DESCRIPTION $message") {
    companion object {
        const val DESCRIPTION = "Firebase Exception (500)."
    }
}



/*

class UnauthorizedException(message: String) : RuntimeException("$DESCRIPTION  $message"){
    companion object {
        const val DESCRIPTION = "Unauthorized Exception (401)."
    }
}
 */





