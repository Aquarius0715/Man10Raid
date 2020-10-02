package aquarius0715.man10raid

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class RaidSystem(private val plugin: Man10Raid) {

    fun changeScoreBoard(player: Player) {

        if (!plugin.sqlManager.sqlConnectSafely()) return

        Thread {

            val sql = "SELECT ScoreBoardStats FROM Man10RaidPlayerTable WHERE StartDate = '${plugin.startDate}' AND UUID = '${player.uniqueId}';"

            val resultSet = plugin.sqlManager.query(sql)

            resultSet!!.next()

            val stats = !resultSet.getBoolean("ScoreBoardStats")

            resultSet.close()

            plugin.scoreBoardStatsMap[player.uniqueId] = stats

            val sql1 = "UPDATE Man10RaidPlayerTable SET ScoreBoardStats = $stats WHERE StartDate = '${plugin.startDate}' AND UUID = '${player.uniqueId}';"

            plugin.sqlManager.execute(sql1)

            plugin.sqlManager.close()

            if (stats) {

                player.sendMessage("${plugin.prefix} スコアボードを${ChatColor.GREEN}${ChatColor.BOLD}表示${ChatColor.WHITE}${ChatColor.BOLD}にしました。")

            } else {

                player.sendMessage("${plugin.prefix} スコアボードを${ChatColor.RED}${ChatColor.BOLD}非表示${ChatColor.WHITE}${ChatColor.BOLD}にしました。")

            }

        }.start()

    }

    fun startRaid(days: Int, hours: Int, minutes: Int, player: Player) {

        if (!plugin.sqlManager.sqlConnectSafely()) return

        plugin.raidStats = true
        plugin.playerData.clear()
        plugin.allScore = 0
        plugin.playerData.clear()
        plugin.startDate = plugin.utils.getStartDate()
        plugin.time = (days * 86400
                + hours * 3600
                + minutes * 60)

        for (player1 in Bukkit.getOnlinePlayers()) {

            plugin.scoreBoardStatsMap[player1.uniqueId] = true

        }

        Bukkit.broadcastMessage("${plugin.prefix} レイドが開始されました！")

        Thread {

            val sql = "INSERT INTO Man10RaidGameTable (StartDate, Time) VALUE ('${plugin.startDate}', ${plugin.time});"

            plugin.sqlManager.execute(sql)

            val sql1 = "SELECT * FROM Man10RaidGameTable WHERE StartDate = '${plugin.startDate}';"

            val resultSet = plugin.sqlManager.query(sql1)

            resultSet!!.next()

            val iD = resultSet.getInt("Id")
            val startDate = resultSet.getString("StartDate")

            player.sendMessage("${plugin.prefix} レイドID:$iD 開始時刻:$startDate レイド期間:${days}日 ${hours}時間 ${minutes}分")
            player.sendMessage("${plugin.prefix} レイドが作成されました！")

        }.start()

    }

    fun pauseRaid() {

        if (!plugin.sqlManager.sqlConnectSafely()) return

        plugin.raidStats = false

        Thread {

            val sql1 = "UPDATE Man10RaidGameTable SET Time = ${plugin.time} WHERE StartDate = '${plugin.startDate}';"

            plugin.sqlManager.execute(sql1)

            plugin.sqlManager.close()

            Bukkit.broadcastMessage("${plugin.prefix} レイドが一時中断されました！")

        }.start()

    }

    fun restartRaid(player: Player) {

        if (!plugin.sqlManager.sqlConnectSafely()) return

        val remain = true

        Thread {

            val sql = "SELECT * FROM Man10RaidGameTable ORDER BY StartDate DESC LIMIT 1"

            val resultSet = plugin.sqlManager.query(sql)

            if (!resultSet!!.next()) {

                player.sendMessage("${plugin.prefix} 前回のデータがSQLに存在しません。")

            }

            resultSet.close()

            plugin.sqlManager.close()

        }.start()

        if (!remain) return

        var successEnd: Boolean

        var time: Int

        var startDate: String

        for (player1 in Bukkit.getOnlinePlayers()) {

            plugin.scoreBoardStatsMap[player1.uniqueId] = true

        }

        Thread {

            val sql = "SELECT * FROM Man10RaidGameTable ORDER BY StartDate DESC LIMIT 1;"

            val resultSet = plugin.sqlManager.query(sql)

            resultSet!!.next()

            successEnd = resultSet.getBoolean("SuccessEnd")

            time = resultSet.getInt("Time")

            startDate = resultSet.getString("StartDate")

            Bukkit.broadcastMessage("取得した時間は $time で、取得した開始時間は $startDate です。また、正常終了は $successEnd です。")

            if (!successEnd) {

                plugin.raidStats = true

                plugin.time = time

                plugin.startDate = startDate

                getScore()

                Bukkit.broadcastMessage("${plugin.prefix}レイドが再開されました！")

            } else {

                player.sendMessage("${plugin.prefix} 前回のレイドは正常に終了しています。")

            }

            resultSet.close()

            plugin.sqlManager.close()

        }.start()

    }

    fun stopRaid() {

        if (!plugin.sqlManager.sqlConnectSafely()) return

        Thread {

            var sql = "insert into Man10RaidHistoryTable (StartDate"

            for ((count, playerData) in plugin.playerData.withIndex()) {

                sql += ", ${plugin.utils.numberToString(count)}"

            }

            sql += ") VALUE ('${plugin.startDate}'"

            for ((count, ranking) in plugin.playerData.withIndex()) {

                sql += ", '第${count + 1}位 スコア: ${ranking.score}点 名前: ${ranking.playerName}'"

            }

            sql += ");"

            val sql1 = "UPDATE Man10RaidGameTable SET SuccessEnd = true;"

            plugin.sqlManager.execute(sql)

            plugin.sqlManager.execute(sql1)

        }.start()

        plugin.time = -1
        plugin.raidStats = false

        Bukkit.broadcastMessage("${plugin.prefix} レイドが終了しました！")

    }

    fun resetScore() {

        if (!plugin.sqlManager.sqlConnectSafely()) return

        Thread {

            val sql = "UPDATE Man10RaidPlayerTable SET Count = 0 WHERE StartDate = '${plugin.startDate}';"

            plugin.sqlManager.execute(sql)
            plugin.sqlManager.close()
            plugin.playerData.clear()
            plugin.allScore = 0

        }.start()

    }

    fun addScore(number: Int, player: Player) {

        if (!plugin.sqlManager.sqlConnectSafely()) return

        checkField(player)

        Thread {

            val sql2 = "UPDATE Man10RaidPlayerTable SET Count = Count + $number " +
                    "WHERE StartDate = '${plugin.startDate}' " +
                    "AND UUID = '${player.uniqueId}';"

            plugin.sqlManager.execute(sql2)

            player.sendMessage("${plugin.prefix}${ChatColor.GRAY}スコアが${number}増えました。")

            getScore()

            val sql1 = "UPDATE Man10RaidGameTable SET Time = ${plugin.time} WHERE StartDate = '${plugin.startDate}';"

            plugin.sqlManager.execute(sql1)

            plugin.sqlManager.close()

        }.start()

    }

    fun setScore(number: Int, player: Player) {

        if (!plugin.sqlManager.sqlConnectSafely()) return

        checkField(player)

        Thread {

            val sql = "UPDATE Man10RaidPlayerTable SET Count = $number " +
                    "WHERE StartDate = '${plugin.startDate}' " +
                    "AND UUID = '${player.uniqueId}';"

            plugin.sqlManager.execute(sql)

            plugin.sqlManager.close()

            getScore()

            val sql1 = "UPDATE Man10RaidGameTable SET Time = ${plugin.time} WHERE StartDate = '${plugin.startDate}';"

            plugin.sqlManager.execute(sql1)

            plugin.sqlManager.close()

        }.start()

        player.sendMessage("${plugin.prefix}${ChatColor.GRAY}あなたのスコアを${number}に設定しました。")

    }

    fun checkResultNow(player: Player) {

        player.sendMessage("${plugin.prefix}現在の上位10名")

        for ((count, playerData) in plugin.playerData.withIndex()) {

            player.sendMessage("${plugin.prefix} ${count + 1}位: ${playerData.playerName}")

        }

    }

    fun checkResultLatest(player: Player) {

        if (!plugin.sqlManager.sqlConnectSafely()) return

        player.sendMessage("${plugin.prefix}前回の上位10名")

        Thread {

            val sql = "SELECT First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth " +
                    "FROM Man10RaidHistoryTable ORDER BY StartDate DESC LIMIT 1;"

            val resultSet = plugin.sqlManager.query(sql)

            var count = 0

            if (!resultSet!!.next()) return@Thread

            while (resultSet.next()) {

                val num = plugin.utils.numberToString(count)

                player.sendMessage("${plugin.prefix} ${resultSet.getString(num)}")

                count++

            }

            resultSet.close()

            plugin.sqlManager.close()

        }.start()

    }

    private fun getScore() {

        if (!plugin.sqlManager.sqlConnectSafely()) return

        Thread {

            val sql3 = "SELECT PlayerName, Count FROM Man10RaidPlayerTable WHERE StartDate = '${plugin.startDate}' ORDER BY Count DESC LIMIT 10;"

            val resultSet2 = plugin.sqlManager.query(sql3)

            plugin.playerData.clear()

            while (resultSet2!!.next()) {

                val playerName: String = if (resultSet2.getString("PlayerName").length < 11) {

                    resultSet2.getString("PlayerName")

                } else {

                    resultSet2.getString("PlayerName").substring(0, 10)

                }

                plugin.playerData.add(Man10Raid.PlayerData(playerName = playerName, score = resultSet2.getInt("Count")))

            }

            resultSet2.close()

            val sql4 = "SELECT SUM(Count) FROM Man10RaidPlayerTable WHERE StartDate = '${plugin.startDate}';"

            val resultSet4 = plugin.sqlManager.query(sql4)

            resultSet4!!.next()

            plugin.allScore = resultSet4.getInt("SUM(Count)")

            resultSet4.close()

            plugin.sqlManager.close()

        }.start()

    }

    private fun checkField(player: Player) {

        if (!plugin.sqlManager.sqlConnectSafely()) return

        Thread {

            val sql = "select PlayerName from Man10RaidPlayerTable " +
                    "where StartDate = '${plugin.startDate}' and UUID = '${player.uniqueId}';"

            val resultSet = plugin.sqlManager.query(sql)

            if (!resultSet!!.next()) {

                val sql1 = "INSERT INTO Man10RaidPlayerTable (StartDate, UUID, PlayerName, Count) " +
                        "VALUE ('${plugin.startDate}', '${player.uniqueId}', '${player.name}', 0);"

                plugin.sqlManager.execute(sql1)

            }

            resultSet.close()

            plugin.sqlManager.close()

        }.start()

    }

}