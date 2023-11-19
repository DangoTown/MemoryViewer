/*
 * Copyright 2023 RTAkland
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package cn.rtast.memoryviewer

import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting


class MemoryViewer : DedicatedServerModInitializer {

    companion object {
        var counter = 0
    }

    private fun getTotalMem(): Int {
        return (Runtime.getRuntime().totalMemory() / (1024 * 1024)).toInt()
    }

    private fun getFreeMem(): Int {
        return (Runtime.getRuntime().freeMemory() / (1024 * 1024)).toInt()
    }

    private fun getUsedMem(): Int {
        return this.getTotalMem() - this.getFreeMem()
    }

    private fun getHeader(): MutableText {
        return Text.literal("Memory Viewer ")
            .styled { it.withColor(Formatting.DARK_PURPLE).withItalic(true).withBold(true) }.append("made by ")
            .append(Text.literal("RTAkland").styled {
                it.withColor(Formatting.AQUA).withItalic(true).withBold(true)
            }).styled {
                it.withColor(Formatting.GREEN).withItalic(true)
            }
    }

    private fun getFooter(): MutableText {
        return Text.empty().append(Text.literal(this.getUsedMem().toString() + "M").styled {
            it.withColor(Formatting.GREEN).withItalic(true)
        }).append(Text.literal("/").styled { it.withColor(Formatting.BLACK).withItalic(true).withBold(true) })
            .append(Text.literal(this.getTotalMem().toString() + "M").styled {
                it.withColor(Formatting.DARK_PURPLE).withItalic(true)
            })
    }

    override fun onInitializeServer() {
        ServerTickEvents.END_SERVER_TICK.register { tick ->
            counter++
            if (counter <= 2000) {
                val playerManager = tick.playerManager
                playerManager.playerList.forEach { _ ->
                    tick.playerManager.sendToAll(PlayerListHeaderS2CPacket(this.getHeader(), this.getFooter()))
                }
                counter = 0
            }
        }
    }
}
