package com.siarheikrupenich.trailermobile.core.network.errorhandling

import com.google.gson.Gson
import com.siarheikrupenich.trailermobile.core.network.data.NetworkError
import com.siarheikrupenich.trailermobile.core.network.data.NetworkException
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

internal class ResultCall<T>(
    private val gson: Gson,
    private val delegate: Call<T>
) : Call<Result<T>> {

    override fun enqueue(callback: Callback<Result<T>>) {
        delegate.enqueue(createResultCallback(callback))
    }

    private fun createResultCallback(callback: Callback<Result<T>>) = object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            handleResponse(response, response.body(), callback)
        }

        override fun onFailure(call: Call<T>, throwable: Throwable) {
            val networkError = when (throwable) {
                is NetworkException -> throwable.error
                is IOException -> NetworkError.NetworkLost
                else -> NetworkError.Unknown(throwable)
            }
            handleFail(callback, networkError)
        }
    }

    private fun handleFail(
        callback: Callback<Result<T>>,
        networkError: NetworkError
    ) {
        callback.onResponse(
            this@ResultCall,
            Response.success(Result.failure(NetworkException(networkError)))
        )
    }

    private fun handleResponse(
        response: Response<T>,
        body: T?,
        callback: Callback<Result<T>>
    ) {
        if (response.isSuccessful && body != null) {
            callback.onResponse(
                this@ResultCall,
                Response.success(response.code(), Result.success(body))
            )
        } else {
            callback.onResponse(
                this@ResultCall,
                Response.success(Result.failure(NetworkException(parseError(response))))
            )
        }
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun execute(): Response<Result<T>> {
        val response = delegate.execute()
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            Response.success(response.code(), Result.success(body))
        } else {
            Response.success(Result.failure(NetworkException(parseError(response))))
        }
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun clone(): Call<Result<T>> = ResultCall(gson, delegate.clone())

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    private fun parseError(response: Response<T>): NetworkError {
        return when (response.code()) {
            401, 403 -> NetworkError.Unauthorized
            else -> {
                val httpException = HttpException(response)
                NetworkError.Unknown(httpException)
            }
        }
    }
}
