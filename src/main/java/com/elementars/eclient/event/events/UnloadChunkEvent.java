package com.elementars.eclient.event.events;

import com.elementars.eclient.event.Event;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.chunk.Chunk;

public class UnloadChunkEvent extends Event {
    private Chunk chunk;

    public UnloadChunkEvent(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
