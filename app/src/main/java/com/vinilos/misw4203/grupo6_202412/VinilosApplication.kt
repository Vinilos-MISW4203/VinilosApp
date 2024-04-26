package com.vinilos.misw4203.grupo6_202412

import android.app.Application
import com.vinilos.misw4203.grupo6_202412.models.repository.VinilosRepository
import com.vinilos.misw4203.grupo6_202412.models.service.VinilosService

class VinilosApplication: Application() {

    private val BASE_URL_API_SERVICE: String = "http://vynils-back-heroku.herokuapp.com/"

    val vinilosService by lazy { VinilosService.getInstance(BASE_URL_API_SERVICE) }
    val vinilosRepository by lazy { VinilosRepository.create(vinilosService) }
}