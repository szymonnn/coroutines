package com.netguru.coroutinesplayground

import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubService {
    @GET("users/{user}/repos")
    suspend fun listRepos(@Path("user") user: String): List<Repo>

    @GET("users/{user}")
    suspend fun user(@Path("user") user: String): User
}

data class Repo (
    val name: String
)

data class User (
    val login: String
)

data class Data(
    val user: User?,
    val repos: List<Repo>?
)