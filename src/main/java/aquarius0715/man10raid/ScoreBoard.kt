package aquarius0715.man10raid

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.ScoreboardManager
import java.util.*

class ScoreBoard(val plugin: Man10Raid) {

    private lateinit var scoreboardManager: ScoreboardManager
    private lateinit var scoreboard: Scoreboard
    private lateinit var objective: Objective

    fun createScoreBoard() {

        scoreboardManager = Bukkit.getScoreboardManager()
        scoreboard = Objects.requireNonNull(scoreboardManager)!!.newScoreboard
        objective = scoreboard.registerNewObjective("man10raid", "Dummy")
        objective.displaySlot = DisplaySlot.SIDEBAR
        objective.displayName = plugin.prefix

    }

    fun updateScoreBoard() {

        object : BukkitRunnable() {

            override fun run() {

                createScoreBoard()

                for (player in Bukkit.getOnlinePlayers()) {

                    player.scoreboard = (Bukkit.getScoreboardManager().newScoreboard)

                }

                if (!plugin.pluginStats || !plugin.raidStats) {

                    return

                }

                if (plugin.time == -1) {

                    plugin.raidSystem.stopRaid()

                    return

                }

                val date = objective.getScore(plugin.utils.calcDate())
                date.score = 13

                val space1 = objective.getScore(" ")
                space1.score = 12

                val allScore = objective.getScore("${ChatColor.BLUE}${ChatColor.BOLD}鯖内総スコア" +
                        "${ChatColor.WHITE}${ChatColor.BOLD} : " +
                        "${ChatColor.GREEN}${ChatColor.BOLD}${plugin.allScore}")
                allScore.score = 11

                val space2 = objective.getScore("  ")
                space2.score = 10

                for ((count, playerData) in plugin.playerData.withIndex()) {

                    val score = objective.getScore("${plugin.utils.choosePrefix(count)}${playerData.playerName} : " +
                            plugin.utils.changeColorInText("${playerData.score}", ChatColor.GREEN))
                    score.score = 9 - count

                }

                for (player in Bukkit.getOnlinePlayers()) {

                    if (!plugin.scoreBoardStatsMap[player.uniqueId]!!) continue

                    player.scoreboard = scoreboard

                }

                if (plugin.time % 60 == 0) {

                    Thread {

                        val sql = "UPDATE Man10RaidGameTable SET Time = ${plugin.time} WHERE StartDate = '${plugin.startDate}';"

                        plugin.sqlManager.execute(sql)

                        plugin.sqlManager.close()

                    }

                }

                plugin.time--

            }

        }.runTaskTimer(plugin, 0, 20)

    }

}