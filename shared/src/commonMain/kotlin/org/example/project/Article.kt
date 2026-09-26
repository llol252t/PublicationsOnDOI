package org.example.project

data class Article(
    val doi: String,
    val title: String,
    val authors: List<String>,
    val year: Int,
    val journal: String,
    val type: String,
    val abstract: String
)