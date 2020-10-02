package aquarius0715.man10raid

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class Man10Raid : JavaPlugin(), Listener {

    var pluginStats = true
    var raidStats = false
    var time = 0
    lateinit var startDate: String

    class PlayerData(var playerName: String, var score: Int)
    val playerData = mutableListOf<PlayerData>()

    var allScore = 0

    val scoreBoardStatsMap = mutableMapOf<UUID, Boolean>()

    val prefix = "${ChatColor.BOLD}[" +
            "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}M" +
            "${ChatColor.WHITE}${ChatColor.BOLD}a" +
            "${ChatColor.GREEN}${ChatColor.BOLD}n" +
            "${ChatColor.WHITE}${ChatColor.BOLD}10" +
            "${ChatColor.AQUA}${ChatColor.BOLD}Raid]"

    val utils = Utils(this)

    val scoreBoard = ScoreBoard(this)
    val sqlManager = MySQLManager(this, "Man10Raid")
    val raidSystem = RaidSystem(this)

    override fun onEnable() {

        getCommand("mraid")!!.setExecutor(Commands(this))

        server.pluginManager.registerEvents(this, this)

        saveDefaultConfig()
        reloadConfig()

        scoreBoard.createScoreBoard()
        scoreBoard.updateScoreBoard()

    }

    override fun onDisable() {

        Thread {

            val sql1 = "UPDATE Man10RaidGameTable SET Time = $time WHERE StartDate = '${startDate}';"

            sqlManager.execute(sql1)

            sqlManager.close()

        }.start()

    }

    @EventHandler

    fun onJoin(event: PlayerJoinEvent) {

        if (raidStats) {

            event.player.sendMessage("$prefix 只今レイドが行われています！")

            scoreBoardStatsMap.putIfAbsent(event.player.uniqueId, true)

        }

    }

}