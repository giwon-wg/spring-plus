package com.example.client

import com.example.client.dto.WeatherDto
import com.example.domain.common.exception.ServerException
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class WeatherClient(
    builder: RestTemplateBuilder
) {
    private val restTemplate = builder.build()

    val todayWeather: String
        get() {
            val responseEntity =
                restTemplate.getForEntity(
                    buildWeatherApiUri(),
                    Array<WeatherDto>::class.java
                )

            if (HttpStatus.OK != responseEntity.statusCode) {
                throw ServerException("날씨 데이터를 가져오는데 실패했습니다. 상태 코드: " + responseEntity.statusCode)
            }

            val weatherArray = responseEntity.body
                ?: throw ServerException("날씨 데이터가 없습니다.")


            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd"))

            return weatherArray.firstOrNull { it.date == today }?.weather
                ?: throw ServerException("오늘에 해당하는 날씨 데이터를 찾을 수 없습니다.")
        }

    private fun buildWeatherApiUri(): URI =
        UriComponentsBuilder
            .fromUriString("https://f-api.github.io")
            .path("/f-api/weather.json")
            .encode()
            .build()
            .toUri()
}
