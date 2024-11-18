package io.oikkani.lfr

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
class LostarkForRiceApplication

fun main(args: Array<String>) {
	runApplication<LostarkForRiceApplication>(*args)
}
