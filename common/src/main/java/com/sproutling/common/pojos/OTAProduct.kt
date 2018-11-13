package com.sproutling.common.pojos


data class OTAProduct(
        val updates: List<Update>
) {

    data class Update(
            val filename_a: String,
            val filename_b: String,
            val version: String,
            val apiVersion: String,
            val sha_a: String,
            val sha_b: String,
            val signature_a: String,
            val signature_b: String,
            val releaseDate: String
    )
}