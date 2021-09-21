package com.willfp.ecoskills.data.storage

import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stats
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Suppress("UNCHECKED_CAST")
class MySQLDataHandler(
    private val plugin: EcoSkillsPlugin
) : DataHandler {
    init {
        Database.connect(
            "jdbc:mysql://" +
                    "${plugin.configYml.getString("mysql.host")}:" +
                    "${plugin.configYml.getString("mysql.port")}/" +
                    plugin.configYml.getString("mysql.database"),
            driver = "com.mysql.jdbc.Driver",
            user = plugin.configYml.getString("mysql.user"),
            password = plugin.configYml.getString("mysql.password")
        )

        for (skill in Skills.values()) {
            Players.registerColumn<IntegerColumnType>(skill.id, IntegerColumnType())
            Players.registerColumn<DoubleColumnType>(skill.xpKey.key, DoubleColumnType())
        }

        for (stat in Stats.values()) {
            Players.registerColumn<IntegerColumnType>(stat.id, IntegerColumnType())
        }

        for (effect in Effects.values()) {
            Players.registerColumn<IntegerColumnType>(effect.id, IntegerColumnType())
        }

        transaction {
            SchemaUtils.create(Players)
        }
    }

    override fun save() {
        // Do nothing
    }

    override fun <T> write(uuid: UUID, key: String, value: T) {
        transaction {
            val player = Players.select { Players.id eq uuid }.firstOrNull() ?: return@transaction
            player[Players.columns.stream().filter { it.name == key }.findFirst().get()] = value
        }
    }

    override fun readInt(uuid: UUID, key: String): Int {
        var value = 0
        transaction {
            val player = Players.select { Players.id eq uuid }.firstOrNull() ?: return@transaction
            value = player[Players.columns.stream().filter { it.name == key }.findFirst().get()] as Int? ?: 0
        }
        return value
    }

    override fun readDouble(uuid: UUID, key: String): Double {
        var value = 0.0
        transaction {
            val player = Players.select { Players.id eq uuid }.firstOrNull() ?: return@transaction
            value = player[Players.columns.stream().filter { it.name == key }.findFirst().get()] as Double? ?: 0.0
        }
        return value
    }

    override fun readString(uuid: UUID, key: String, default: String): String {
        var value = ""
        transaction {
            val player = Players.select { Players.id eq uuid }.firstOrNull() ?: return@transaction
            value = player[Players.columns.stream().filter { it.name == key }.findFirst().get()] as String? ?: ""
        }
        return value
    }

    object Players : UUIDTable("EcoSkills_Players") {
        override val id = uuid("id")
            .entityId()
        val name = varchar("name", 50)
            .default("Unknown Player")
    }
}