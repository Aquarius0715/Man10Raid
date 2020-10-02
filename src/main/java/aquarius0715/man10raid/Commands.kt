package aquarius0715.man10raid

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.NumberFormatException

class Commands(private val plugin: Man10Raid): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {

            sender.sendMessage("${plugin.prefix}あなたにはこのコマンドを実行する権限があります。")

            return true

        }

        when (label) {

            "mraid" -> when (args.size) {

                0 -> {

                    if (plugin.pluginStats) {

                        sender.sendMessage("${plugin.prefix} Man10RaidPluginは ${ChatColor.GREEN}${ChatColor.BOLD}有効 ${ChatColor.WHITE}${ChatColor.BOLD}です。")

                    } else {

                        sender.sendMessage("${plugin.prefix} Man10RaidPluginは ${ChatColor.RED}${ChatColor.BOLD}無効 ${ChatColor.WHITE}${ChatColor.BOLD}です。")

                    }

                    sender.sendMessage("${plugin.prefix} Type /mraid help")
                    sender.sendMessage("${plugin.prefix} Version 1.0.0")
                    sender.sendMessage("${plugin.prefix} Created by Aquarius0715")

                    return true

                }

                1 -> {

                    when (args[0]) {

                        "help" -> {

                            showHelp(1, sender)

                        }

                        "start" -> {

                            if (!plugin.pluginStats) {

                                sender.sendMessage("${plugin.prefix} プラグインが${ChatColor.RED}${ChatColor.BOLD}オフ${ChatColor.WHITE}${ChatColor.BOLD}になっています。")

                                return true

                            } else if (!sender.hasPermission("mraid.admin")) {

                                sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}あなたはこのコマンドを実行する権限がありません。")

                                return true

                            } else if (plugin.raidStats) {

                                sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}既にレイドが始まっています。")

                                return true

                            } else {

                                sender.sendMessage("${plugin.prefix} /start [日] [時間] [分]")

                            }

                        }

                        "sb" -> {

                            if (!plugin.pluginStats) {

                                sender.sendMessage("${plugin.prefix} プラグインが${ChatColor.RED}${ChatColor.BOLD}オフ${ChatColor.WHITE}${ChatColor.BOLD}になっています。")

                                return true

                            } else if (!plugin.raidStats) {

                                sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}レイドが始まっていません。")

                                return true

                            } else {

                                plugin.raidSystem.changeScoreBoard(sender)

                                return true

                            }

                        }

                        "pause" -> {

                            if (!plugin.pluginStats) {

                                sender.sendMessage("${plugin.prefix} プラグインが${ChatColor.RED}${ChatColor.BOLD}オフ${ChatColor.WHITE}${ChatColor.BOLD}になっています。")

                                return true

                            } else if (!sender.hasPermission("mraid.admin")) {

                                sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}あなたはこのコマンドを実行する権限がありません。")

                                return true

                            } else if (!plugin.raidStats) {


                                sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}レイドが始まっていません。")

                                return true

                            } else {

                                plugin.raidSystem.pauseRaid()

                                return true

                            }

                        }

                        "restart" -> {

                            if (!plugin.pluginStats) {

                                sender.sendMessage("${plugin.prefix} プラグインが${ChatColor.RED}${ChatColor.BOLD}オフ${ChatColor.WHITE}${ChatColor.BOLD}になっています。")

                                return true

                            } else if (!sender.hasPermission("mraid.admin")) {

                                sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}あなたはこのコマンドを実行する権限がありません。")

                                return true

                            } else if (plugin.raidStats) {

                                sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}既にレイドが始まっています。")

                                return true

                            } else {

                                plugin.raidSystem.restartRaid(sender)

                                return true

                            }

                        }

                        "stop" -> {

                            if (!plugin.pluginStats) {

                                sender.sendMessage("${plugin.prefix} プラグインが${ChatColor.RED}${ChatColor.BOLD}オフ${ChatColor.WHITE}${ChatColor.BOLD}になっています。")

                                return true

                            } else if (!sender.hasPermission("mraid.admin")) {

                                sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}あなたはこのコマンドを実行する権限がありません。")

                                return true

                            } else if (!plugin.raidStats) {

                                sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}レイドが始まっていません。")

                                return true

                            } else {

                                plugin.raidSystem.stopRaid()

                                return true

                            }

                        }

                        "resetscore" -> {

                            if (!plugin.pluginStats) {

                                sender.sendMessage("${plugin.prefix} プラグインが${ChatColor.RED}${ChatColor.BOLD}オフ${ChatColor.WHITE}${ChatColor.BOLD}になっています。")

                                return true

                            } else if (!sender.hasPermission("mraid.admin")) {

                                sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}あなたはこのコマンドを実行する権限がありません。")

                                return true

                            } else if (!plugin.raidStats) {

                                sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}レイドが始まっていません。")

                                return true

                            } else {

                                plugin.raidSystem.resetScore()

                                return true

                            }

                        }

                        "on" -> {

                            if (!sender.hasPermission("mraid.admin")) {

                                sender.sendMessage("${plugin.prefix} あなたはこのコマンドを実行する権限がありません。")

                                return true

                            }

                            if (plugin.pluginStats) {

                                sender.sendMessage("${plugin.prefix}すでにプラグインはオンになっています。")

                                return true

                            } else {

                                sender.sendMessage("${plugin.prefix}プラグインをオンにしました。")

                                plugin.pluginStats = true

                                return true

                            }

                        }

                        "off" -> {

                            if (!sender.hasPermission("mraid.admin")) {

                                sender.sendMessage("${plugin.prefix} あなたはこのコマンドを実行する権限がありません。")

                                return true

                            }

                            if (!plugin.pluginStats) {

                                sender.sendMessage("${plugin.prefix}すでにプラグインはオフになっています。")

                                return true

                            } else {

                                sender.sendMessage("${plugin.prefix}プラグインをオフにしました。")

                                plugin.pluginStats = false

                                return true

                            }

                        }

                    }

                }

                2 -> {

                    when (args[0]) {

                        "help" -> {

                            when (args[1]) {

                                "1" -> {

                                    showHelp(1, sender)

                                }

                                "2" -> {

                                    showHelp(2, sender)

                                }

                            }

                        }

                        "addscore" -> {

                            if (!plugin.pluginStats) {

                                sender.sendMessage("${plugin.prefix} プラグインが${ChatColor.RED}${ChatColor.BOLD}オフ${ChatColor.WHITE}${ChatColor.BOLD}になっています。")

                                return true

                            } else if (!sender.hasPermission("mraid.admin")) {

                                sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}あなたはこのコマンドを実行する権限がありません。")

                                return true

                            } else if (!plugin.raidStats) {

                                sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}レイドが始まっていません。")

                                return true

                            } else {

                                try {

                                    args[1].toInt()

                                } catch (e: NumberFormatException) {

                                    return true

                                }

                                plugin.raidSystem.addScore(args[1].toInt(), sender)

                                return true

                            }

                        }

                        "setscore" -> {

                            if (!plugin.pluginStats) {

                                sender.sendMessage("${plugin.prefix} プラグインが${ChatColor.RED}${ChatColor.BOLD}オフ${ChatColor.WHITE}${ChatColor.BOLD}になっています。")

                                return true

                            } else if (!sender.hasPermission("mraid.admin")) {

                                sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}あなたはこのコマンドを実行する権限がありません。")

                                return true

                            } else if (!plugin.raidStats) {

                                sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}レイドが始まっていません。")

                                return true

                            } else {

                                try {

                                    args[1].toInt()

                                } catch (e: NumberFormatException) {

                                    sender.sendMessage("${plugin.prefix}数字を入力してください。")

                                    return true

                                }

                                plugin.raidSystem.setScore(args[1].toInt(), sender)

                                return true

                            }

                        }

                        "result" -> {

                            when (args[1]) {

                                "now" -> {

                                    if (!plugin.pluginStats) {

                                        sender.sendMessage("${plugin.prefix} プラグインが${ChatColor.RED}${ChatColor.BOLD}オフ${ChatColor.WHITE}${ChatColor.BOLD}になっています。")

                                        return true

                                    } else if (!sender.hasPermission("mraid.admin")) {

                                        sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}あなたはこのコマンドを実行する権限がありません。")

                                        return true

                                    } else if (!plugin.raidStats) {

                                        sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}レイドが始まっていません。")

                                        return true

                                    } else {

                                        plugin.raidSystem.checkResultNow(sender)

                                        return true

                                    }

                                }

                                "latest" -> {

                                    if (!plugin.pluginStats) {

                                        sender.sendMessage("${plugin.prefix} プラグインが${ChatColor.RED}${ChatColor.BOLD}オフ${ChatColor.WHITE}${ChatColor.BOLD}になっています。")

                                        return true

                                    } else if (!sender.hasPermission("mraid.admin")) {

                                        sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}あなたはこのコマンドを実行する権限がありません。")

                                        return true

                                    } else {

                                        plugin.raidSystem.checkResultLatest(sender)

                                        return true

                                    }

                                }

                            }

                        }

                    }

                }

                4 -> {

                    when (args[0]) {

                        "start" -> {

                            if (!plugin.pluginStats) {

                                sender.sendMessage("${plugin.prefix} プラグインが${ChatColor.RED}${ChatColor.BOLD}オフ${ChatColor.WHITE}${ChatColor.BOLD}になっています。")

                                return true

                            } else if (!sender.hasPermission("mraid.admin")) {

                                sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}あなたはこのコマンドを実行する権限がありません。")

                                return true

                            } else if (plugin.raidStats) {

                                sender.sendMessage("${plugin.prefix} ${ChatColor.RED}${ChatColor.BOLD}既にレイドが始まっています。")

                                return true

                            } else {

                                try {

                                    args[1].toInt()
                                    args[2].toInt()
                                    args[3].toInt()

                                } catch (e: NumberFormatException) {

                                    sender.sendMessage("${plugin.prefix} 数字を入力してください。")

                                    return true

                                }

                                plugin.raidSystem.startRaid(args[1].toInt(), args[2].toInt(), args[3].toInt(), sender)

                                return true

                            }

                        }

                    }

                }

            }

        }

        return false

    }

    private fun showHelp(page: Int, sender: Player) {

        when (page) {

            1 -> {

                sender.sendMessage("${plugin.prefix}Page 1 / 2")
                sender.sendMessage("${plugin.prefix}</mraid sb> : スコアボードの表示を切り替えます。")
                sender.sendMessage("${plugin.prefix}</mraid start [日] [時間] [分]> : レイドをスタートします。")
                sender.sendMessage("${plugin.prefix}</mraid pause> : レイドを一時中断します。(レイド再開ができます。)")
                sender.sendMessage("${plugin.prefix}</mraid restart> : レイドを一時中断した後に、再開します。")
                sender.sendMessage("${plugin.prefix}</mraid stop> : レイドを強制終了します。(レイド再開ができません。)")
                sender.sendMessage("${plugin.prefix}</mraid on> : このプラグインをオンにします。")
                sender.sendMessage("${plugin.prefix}</mraid off> : このプラグインをオフにします。")
                sender.sendMessage("${plugin.prefix}</mraid addscore [number]> : 指定したnumber分スコアを増やします。")
                sender.sendMessage("${plugin.prefix}</mraid setscore [number]> : 指定したnumberにスコアを指定します。")
                sender.sendMessage("${plugin.prefix}</mraid resetscore> : 現在進行しているレイドのスコアをリセットします。")

            }

            2 -> {

                sender.sendMessage("${plugin.prefix}Page 2 / 2")
                sender.sendMessage("${plugin.prefix}</mraid result now> : 現在行われいるレイドの上位10名の結果を表示します。")
                sender.sendMessage("${plugin.prefix}</mraid result latest> : 前回行われたレイドの上位10名の結果を表示します。")

            }

        }

    }

}