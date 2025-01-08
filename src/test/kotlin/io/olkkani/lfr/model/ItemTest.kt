package io.olkkani.lfr.model

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
    val items = mutableListOf<Item>()
    var item = Item(1)
    item.addTodayPrices(initData)

    items.add(item)
    var findItem: Item = items.find { it -> it.itemCode == 1 }!!


    println("open ${findItem.open}")
    println("close ${findItem.close}")
    println("high ${findItem.high}")
    println("low ${findItem.low}")

    if(items.isNotEmpty()){
        println("not empty")
    }
    items.clear()
    if(items.isEmpty()){
        println("empty")
    }
    initData = ItemTest().createInitData(10, 20)
    item = Item(2)
    item.addTodayPrices(initData)
    items.add(item)

    findItem = items.find { it -> it.itemCode == 2 }!!
    println("open ${findItem.open}")
    println("close ${findItem.close}")
    println("high ${findItem.high}")
    println("low ${findItem.low}")
    println("----")
    initData = ItemTest().createInitData(1, 10)
    items.find { it -> it.itemCode == 2 }!!.addTodayPrices(initData)
    findItem = items.find { it -> it.itemCode == 2 }!!
    println("open ${findItem.open}")
    println("close ${findItem.close}")
    println("high ${findItem.high}")
    println("low ${findItem.low}")

}

