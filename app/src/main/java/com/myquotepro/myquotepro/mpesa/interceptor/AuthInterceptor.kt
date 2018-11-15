package com.myquotepro.myquotepro.mpesa.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * This class add information an authorization key to [okhttp3.OkHttpClient] which is passed in
 * {ApiClient#getRestAdapter} which is required when making a request.
 *
 * @author Thomas Kioko
 */
class AuthInterceptor(private val mAuthToken: String) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $mAuthToken")
            .build()
        return chain.proceed(request)
    }
}
