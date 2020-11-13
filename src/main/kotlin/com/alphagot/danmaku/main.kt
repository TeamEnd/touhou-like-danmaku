package com.alphagot.danmaku

import com.sk89q.worldedit.math.Vector3
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.lang.NullPointerException
import com.github.noonmaru.tap.fake.*
import org.bukkit.Location

class main(): JavaPlugin(){
    var danmaku = mutableMapOf<String, List<command>>()
    var bullets = mutableMapOf<String, danmakuType>()
    override fun onEnable() {
        logger.info("Init...(#-1)")
        try{
            val scriptsList = File("./plugins/danmaku/scripts/").list()!!
            scriptsList.forEach{ itt ->
                val tmp = parser(itt).parse()
                tmp.forEach{
                    if(it.command == commandType.meta){
                        if(it.params[0] == "name" && it.params.size >= 2){
                            danmaku[it.params[1]] = tmp
                        }
                    }
                }
            }
            if(scriptsList.size != danmaku.size){
                logger.warning("Some script's metadata (name) is invalid! (#4)")
            }
        }catch(e: NullPointerException){
            logger.warning("Warning: Script is empty! (#1)")
        }catch (e: Exception){
            logger.warning("Warning: Unknown Error! (#0), Error Report: \n$e")
        }
        logger.info("OK (#2)")
    }

    private fun commandExecutor(c: List<command>, s: CommandSender){
        var i = 0
        c.forEach{ _ ->
            val it = c[i]
            when(it.command){
                commandType.nop, commandType.comment -> {}
                commandType.play_sound -> {
                    val w = (s as Player).world
                    w.playSound((s).location, it.params[0], 1f, 1f)
                }
                commandType.stop_sound -> {
                    (s as Player).stopSound(it.params[0])
                }
                commandType.jump -> {
                    i = it.params[0].toInt()
                }
                commandType.text -> {
                    val name = it.params[0].replace("_", " ")
                    val text = it.params.subList(1, it.params.size - 1).joinToString(" ")
                    val finalString = StringBuilder()
                            .append("${ChatColor.DARK_AQUA}$name\n")
                            .append("\t${ChatColor.DARK_GREEN}$text")
                    s.sendMessage(finalString.substring(0))
                }
                commandType.danmaku_addtype -> {
                    bullets[it.params[0]] = danmakuType(it.params[0], it.params[1], Vector3.at(it.params[2].toDouble(), it.params[3].toDouble(), it.params[4].toDouble()))
                }
                commandType.image -> TODO()
                commandType.danmaku_launch -> {
                    val r = (bullets[it.params[0]]!!.scale.x + bullets[it.params[0]]!!.scale.y + bullets[it.params[0]]!!.scale.z) / 3
                    val p = FakeProjectile(it.params[1].toInt(), r)
                    p.targetLocation = Location((s as Player).world, it.params[2].toDouble(), it.params[3].toDouble(), it.params[4].toDouble())
                }
                commandType.meta -> {}
            }
            i++
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        var rv = false
        when(command.name){
            "execpattern" -> {
                rv = if(args.isNotEmpty()){
                    commandExecutor(danmaku[args[0]]!!, sender)
                    true
                }else{
                    false
                }
            }
            "execstage" -> {
                rv = if(args.isNotEmpty()){
                    commandExecutor(danmaku[args[0]]!!, sender)
                    true
                }else{
                    false
                }
            }
        }
        return rv
    }
}