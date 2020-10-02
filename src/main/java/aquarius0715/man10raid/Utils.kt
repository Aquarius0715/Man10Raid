package aquarius0715.man10raid

import org.bukkit.ChatColor
import java.text.SimpleDateFormat
import java.util.*


class Utils(private val plugin: Man10Raid) {

    fun changeColorInText(text: String, chatColor: ChatColor): String {

        return "${chatColor}${ChatColor.BOLD}${text}${ChatColor.WHITE}${ChatColor.BOLD}"

    }

    fun calcDate(): String {

        return "${ChatColor.BLUE}${ChatColor.BOLD}残り: " +
                "${ChatColor.YELLOW}${plugin.time / 86400}" +
                "${ChatColor.WHITE}日" +
                "${ChatColor.YELLOW}${plugin.time % 86400 / 3600}" +
                "${ChatColor.WHITE}時間" +
                "${ChatColor.YELLOW}${plugin.time % 86400 % 3600 / 60}" +
                "${ChatColor.WHITE}分" +
                "${ChatColor.YELLOW}${plugin.time % 86400 % 3600 % 60}" +
                "${ChatColor.WHITE}秒"

    }

    fun choosePrefix(count: Int): String {

        return when (count) {

            0 -> changeColorInText("♔第1位♔ ", ChatColor.YELLOW)
            1 -> changeColorInText("♕第2位♕ ", ChatColor.GRAY)
            2 -> changeColorInText("♖第3位♖ ", ChatColor.GOLD)
            3 -> changeColorInText("♗第4位♗ ", ChatColor.WHITE)
            4 -> changeColorInText("♘第5位♘ ", ChatColor.WHITE)
            5, 6, 7, 8, 9 -> changeColorInText("♙第${count + 1}位♙ ", ChatColor.WHITE)
            else -> ""

        }

    }

    fun getStartDate(): String {

        val simpleDateFormat = SimpleDateFormat("yyyy'-'MM'-'dd' 'HH':'mm':'ss")

        return simpleDateFormat.format(Date())

    }

    fun numberToString(number: Int): String {

        return when (number) {

            0 -> "First"
            1 -> "Second"
            2 -> "Third"
            3 -> "Fourth"
            4 -> "Fifth"
            5 -> "Sixth"
            6 -> "Seventh"
            7 -> "Eighth"
            8 -> "Ninth"
            9 -> "Tenth"
            else -> "None"

        }

    }

}