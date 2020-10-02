package aquarius0715.man10raid

import org.bukkit.Bukkit
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.logging.Level

/**
 * Created by takatronix on 2017/03/05.
 */
class MySQLManager(private val plugin: Man10Raid, private val conName: String) {
    var debugMode = false
    private var HOST: String? = null
    private var DB: String? = null
    private var USER: String? = null
    private var PASS: String? = null
    private var PORT: String? = null
    private var connected = false
    private var st: Statement? = null
    private var con: Connection? = null
    private var MySQL: MySQLFunc? = null

    /////////////////////////////////
    //       設定ファイル読み込み
    /////////////////////////////////
    fun loadConfig() {
        //   plugin.getLogger().info("MYSQL Config loading");
        plugin.reloadConfig()
        HOST = plugin.config.getString("mysql.host")
        USER = plugin.config.getString("mysql.user")
        PASS = plugin.config.getString("mysql.pass")
        PORT = plugin.config.getString("mysql.port")
        DB = plugin.config.getString("mysql.db")
        //  plugin.getLogger().info("Config loaded");
    }

    fun commit() {
        try {
            con!!.commit()
        } catch (e: Exception) {
        }
    }

    ////////////////////////////////
    //       接続
    ////////////////////////////////
    fun Connect(host: String?, db: String?, user: String?, pass: String?, port: String?): Boolean {
        HOST = host
        DB = db
        USER = user
        PASS = pass
        MySQL = MySQLFunc(host, db, user, pass, port)
        con = MySQL!!.open()
        if (con == null) {
            Bukkit.getLogger().info("failed to open MYSQL")
            return false
        }
        try {
            st = con!!.createStatement()
            connected = true
            plugin.logger.info("[$conName] Connected to the database.")
        } catch (var6: SQLException) {
            connected = false
            plugin.logger.info("[$conName] Could not connect to the database.")
        }
        MySQL!!.close(con)
        return connected
    }

    //////////////////////////////////////////
    //         接続確認
    //////////////////////////////////////////
    fun connectCheck(): Boolean {
        return Connect(HOST, DB, USER, PASS, PORT)
    }

    ////////////////////////////////
    //     行数を数える
    ////////////////////////////////
    fun countRows(table: String): Int {
        var count = 0
        val set = query(String.format("SELECT * FROM %s", table))
        try {
            while (set!!.next()) {
                ++count
            }
        } catch (var5: SQLException) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not select all rows from table: " + table + ", error: " + var5.errorCode)
        }
        return count
    }

    ////////////////////////////////
    //     レコード数
    ////////////////////////////////
    fun count(table: String): Int {
        var count = 0
        val set = query(String.format("SELECT count(*) from %s", table))
        count = try {
            set!!.getInt("count(*)")
        } catch (var5: SQLException) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not select all rows from table: " + table + ", error: " + var5.errorCode)
            return -1
        }
        return count
    }

    ////////////////////////////////
    //      実行
    ////////////////////////////////
    fun execute(query: String): Boolean {
        MySQL = MySQLFunc(HOST, DB, USER, PASS, PORT)
        con = MySQL!!.open()
        if (con == null) {
            Bukkit.getLogger().info("failed to open MYSQL")
            return false
        }
        var ret = true
        if (debugMode) {
            plugin.logger.info("query:$query")
        }
        try {
            st = con!!.createStatement()
            st!!.execute(query)
        } catch (var3: SQLException) {
            plugin.logger.info("[" + conName + "] Error executing statement: " + var3.errorCode + ":" + var3.localizedMessage)
            plugin.logger.info(query)
            ret = false
        }
        close()
        return ret
    }

    ////////////////////////////////
    //      クエリ
    ////////////////////////////////
    fun query(query: String): ResultSet? {
        MySQL = MySQLFunc(HOST, DB, USER, PASS, PORT)
        con = MySQL!!.open()
        var rs: ResultSet? = null
        if (con == null) {
            Bukkit.getLogger().info("failed to open MYSQL")
            return rs
        }
        if (debugMode) {
            plugin.logger.info("[DEBUG] query:$query")
        }
        try {
            st = con!!.createStatement()
            rs = st!!.executeQuery(query)
        } catch (var4: SQLException) {
            plugin.logger.info("[" + conName + "] Error executing query: " + var4.errorCode)
            plugin.logger.info(query)
        }

//        this.close();
        return rs
    }

    fun close() {
        try {
            st!!.close()
            con!!.close()
            MySQL!!.close(con)
        } catch (var4: SQLException) {
        }
    }

    ////////////////////////////////
    //      コンストラクタ
    ////////////////////////////////
    init {
        connected = false
        loadConfig()
        connected = Connect(HOST, DB, USER, PASS, PORT)
        if (!connected) {
            plugin.logger.info("Unable to establish a MySQL connection.")
        }
        execute("create table if not exists Man10RaidPlayerTable(" +
                "Id int auto_increment not null primary key," +
                "StartDate VARCHAR(19)," +
                "UUID VARCHAR(36)," +
                "PlayerName VARCHAR(16)," +
                "Count INT NOT NULL DEFAULT 0," +
                "ScoreBoardStats BOOLEAN NOT NULL DEFAULT true);")

        execute("create table if not exists Man10RaidGameTable(" +
                "Id int auto_increment not null primary key," +
                "StartDate VARCHAR(19)," +
                "Time int not null default 1," +
                "SuccessEnd boolean not null default false);")

        execute("create table if not exists Man10RaidHistoryTable(" +
                "Id int auto_increment not null primary key," +
                "StartDate VARCHAR(19)," +
                "First VARCHAR(64)," +
                "Second VARCHAR(64)," +
                "Third VARCHAR(64)," +
                "Fourth VARCHAR(64)," +
                "Fifth VARCHAR(64)," +
                "Sixth VARCHAR(64)," +
                "Seventh VARCHAR(64)," +
                "Eighth VARCHAR(64)," +
                "Ninth VARCHAR(64)," +
                "Tenth VARCHAR(64));")

    }

    fun sqlConnectSafely(): Boolean{

        if (!connectCheck()) {

            Bukkit.broadcastMessage("${plugin.prefix}データベースに正常に接続できませんでした。プラグインを停止します。")

            plugin.raidStats = false

            plugin.pluginStats = false

            return false

        }

        return true

    }

}