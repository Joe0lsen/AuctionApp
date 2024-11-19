
package com.example.auctionapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class QueryRequest(val query: String)
data class QueryResponse(val data: List<Map<String, Any>>)

interface MotherDuckAPI {
    @POST("/query")
    @Headers("Content-Type: application/json")
    fun executeQuery(@Body query: QueryRequest): Call<QueryResponse>
}
