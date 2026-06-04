package com.example.a207408_cikguizwan_lab02

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// 对应 API 返回的数据结构
data class Taxon(
    val name: String,
    val preferred_common_name: String? = null,
    val default_photo: DefaultPhoto? = null
)

data class DefaultPhoto(
    val square_url: String? = null
)

data class Observation(
    val id: Int,
    val taxon: Taxon? = null,
    val place_guess: String? = null,
    val observed_on: String? = null,
    val quality_grade: String? = null
)

data class ObservationResponse(
    val results: List<Observation>
)

// Retrofit 接口
interface INaturalistApi {
    @GET("observations")
    suspend fun getObservations(
        @Query("place_id") placeId: Int = 7161,      // 7161 = Malaysia
        @Query("quality_grade") qualityGrade: String = "research",
        @Query("per_page") perPage: Int = 20,
        @Query("order_by") orderBy: String = "created_at",
        @Query("has[]") hasPhoto: String = "photos"
    ): ObservationResponse
}

// Retrofit 单例
object RetrofitInstance {
    val api: INaturalistApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.inaturalist.org/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(INaturalistApi::class.java)
    }
}