package io.olkkani.lfr.model

import io.olkkani.lfr.dto.ItemTodayPrices
import java.time.LocalDateTime

class ItemTest {


    fun createInitData (minIndex: Int, maxIndex: Int): MutableMap<LocalDateTime, Int> {
        val initDate: MutableMap<LocalDateTime, Int> = mutableMapOf()
        for(i in minIndex.. maxIndex){
            val localDate = LocalDateTime.now()
            initDate[localDate] = i * 10
        }
        return initDate
    }

//    fun getItem() {
//        return initDate
//    }
}

fun main(args: Array<String>) {
    var initData = ItemTest().createInitData(1, 10)
    val itemTodayPrices = mutableListOf<ItemTodayPrices>()
    var itemTodayPrice = ItemTodayPrices(1)
    itemTodayPrice.addTodayPrices(initData)

    itemTodayPrices.add(itemTodayPrice)
    var findItemTodayPrices: ItemTodayPrices = itemTodayPrices.find { it -> it.itemCode == 1 }!!


    println("open ${findItemTodayPrices.open}")
    println("close ${findItemTodayPrices.close}")
    println("high ${findItemTodayPrices.high}")
    println("low ${findItemTodayPrices.low}")

    if(itemTodayPrices.isNotEmpty()){
        println("not empty")
    }
    itemTodayPrices.clear()
    if(itemTodayPrices.isEmpty()){
        println("empty")
    }
    initData = ItemTest().createInitData(10, 20)
    itemTodayPrice = ItemTodayPrices(2)
    itemTodayPrice.addTodayPrices(initData)
    itemTodayPrices.add(itemTodayPrice)

    findItemTodayPrices = itemTodayPrices.find { it -> it.itemCode == 2 }!!
    println("open ${findItemTodayPrices.open}")
    println("close ${findItemTodayPrices.close}")
    println("high ${findItemTodayPrices.high}")
    println("low ${findItemTodayPrices.low}")
    println("----")
    initData = ItemTest().createInitData(1, 10)
    itemTodayPrices.find { it -> it.itemCode == 2 }!!.addTodayPrices(initData)
    findItemTodayPrices = itemTodayPrices.find { it -> it.itemCode == 2 }!!
    println("open ${findItemTodayPrices.open}")
    println("close ${findItemTodayPrices.close}")
    println("high ${findItemTodayPrices.high}")
    println("low ${findItemTodayPrices.low}")

}

