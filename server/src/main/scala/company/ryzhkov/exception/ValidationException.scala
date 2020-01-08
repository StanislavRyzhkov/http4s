package company.ryzhkov.exception

case class ValidationException(message: String)
    extends RuntimeException(message)
