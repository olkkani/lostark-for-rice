package io.olkkani.lfr

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
//@ComponentScan(basePackages = ["io.olkkani.lfr.util", "io.olkkani.lfr.service"])
class LostarkForRiceApplication

fun main(args: Array<String>) {
	runApplication<LostarkForRiceApplication>(*args)
}
