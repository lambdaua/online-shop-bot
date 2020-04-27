package controllers

open class BaseResponse(val status: String)

open class OkResponse : BaseResponse("ok")

open class ResultResponse<out RESULT>(val result: RESULT) : OkResponse()

open class ErrorResponse(val message: String, val code: Int) : BaseResponse("error")