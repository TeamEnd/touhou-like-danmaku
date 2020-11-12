package com.alphagot.danmaku

import com.sk89q.worldedit.math.Vector3
import java.io.File

enum class commandType{
    nop,
    comment,
    text,
    image,
    danmaku_addtype,
    danmaku_launch,
    jump,
    play_sound,
    stop_sound,
    meta
}

data class command(
    val command: commandType,
    val params: List<String>
)

data class danmakuType(
        val name: String,
        val block: String,
        val scale: Vector3
)

class parser(filename: String) {
    val f: File = File("./plugins/danmaku/scripts/$filename")

    fun str2ct(str: String): commandType {
        when(str){
            "nop", "comment" -> return commandType.nop
            "text" -> return commandType.text
            "image" -> return commandType.image
            "add-danmaku-type" -> return commandType.danmaku_addtype
            "launch" -> return commandType.danmaku_launch
            "jump" -> return commandType.jump
            "play-sound" -> return commandType.play_sound
            "stop-sound" -> return commandType.stop_sound
            "metadata" -> return commandType.meta
        }
        return commandType.nop
    }

    fun parse(): List<command> {
        val scriptArray: List<String> = this.f.readText().split("\n")
        var result: List<command> = listOf()
        scriptArray.forEach{
            val bar = it.replace("${it.split(" ")[0]} ", "")
            val foo = command(str2ct(it.split(" ")[0]), bar.split(" "))
            result = result.plus(foo)
        }
        return result
    }
}