package com.example.hiddengem.util

import org.shredzone.commons.suncalc.SunTimes
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object LightTimes {
    val options = listOf("Sunrise", "Golden hour", "Midday", "Sunset", "Blue hour")

    private val clock = DateTimeFormatter.ofPattern("h:mm a")   // e.g. 5:57 PM

// One chosen tag as a sentence (used by the best-light panel).
    fun sentence(bestTime: String, lat: Double, lng: Double): String {
        if (bestTime.isBlank()) return "Best light: not set for this spot"
        val time = timeFor(bestTime, lat, lng)
        return if (time != null)
            "Best light: around ${bestTime.lowercase()} — today ${time.format(clock)}"
        else
            "Best light: around ${bestTime.lowercase()} (no exact time today at this spot)"
    }

        // All of today's key light times (used by the Week 8 light strip).
        data class Moment(val label: String, val time: String)
        private val stripOrder = listOf("Sunrise", "Midday", "Golden hour", "Sunset", "Blue hour")

        fun dayMoments(lat: Double, lng: Double): List<Moment> =
            stripOrder.mapNotNull { tag ->
                timeFor(tag, lat, lng)?.let { Moment(tag, it.format(clock)) }
            }

        private fun timeFor(bestTime: String, lat: Double, lng: Double): ZonedDateTime? {
            val zone = ZoneId.systemDefault()
            val midnight = ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, zone)
            val noon = ZonedDateTime.of(LocalDate.now(), LocalTime.NOON, zone)
            fun from(start: ZonedDateTime) = SunTimes.compute().on(start).at(lat, lng)

            return when (bestTime) {
                "Sunrise"     -> from(midnight).execute().rise
                "Midday"      -> from(midnight).execute().noon
                "Golden hour" -> from(noon).twilight(SunTimes.Twilight.GOLDEN_HOUR).execute().set
                "Sunset"      -> from(noon).execute().set
                "Blue hour"   -> from(noon).twilight(SunTimes.Twilight.BLUE_HOUR).execute().set
                else          -> null
            }
        }
}

