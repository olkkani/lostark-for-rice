package io.oikkani.integrationservice.domain.dto

enum class DiscordMessageColor(hexCode: String, colorNumber: Int) {
    RED("#FF0000", 16711680),
    YELLOW("#FFFF00", 16776960),
    GREEN("#00FF00", 65280),
    BLUE("#0000FF", 255)
}