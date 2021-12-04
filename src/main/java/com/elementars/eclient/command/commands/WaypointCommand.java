package com.elementars.eclient.command.commands;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.command.CommandManager;
import com.elementars.eclient.module.render.Waypoints;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * @author Elementars
 * @since 6/24/2020 - 10:49 AM
 */
public class WaypointCommand extends Command {
    public WaypointCommand() {
        super("waypoints", "Manages Waypoints", new String[]{"add", "remove"});
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            sendChatMessage("Use .waypoints help to see commands");
            return;
        }
        if (args[1].equalsIgnoreCase("add")) {
            if (args.length < 3) {
                sendChatMessage("Please specify the name of the waypoint (.waypoints add name (X) (Y) (Z))");
                return;
            }
            if (args.length < 6) {
                sendChatMessage("Please specify coordinates (.waypoints add name (X) (Y) (Z))");
                return;
            }
            final int x = (int) Double.parseDouble(args[3]);
            final int y = (int) Double.parseDouble(args[4]);
            final int z = (int) Double.parseDouble(args[5]);
            Waypoints.WAYPOINTS.add(new Waypoints.Waypoint(UUID.randomUUID(), args[2], new BlockPos(x, y, z), mc.player.getEntityBoundingBox(), mc.player.dimension));
            sendChatMessage("Added waypoint with the name: " + args[2]);
            return;
        }
        if (args[1].equalsIgnoreCase("remove")) {
            if (args.length < 3) {
                sendChatMessage("Please specify the name of the waypoint (.waypoints remove name)");
                return;
            }
            Waypoints.WAYPOINTS.removeIf(waypoint -> waypoint.getName().equalsIgnoreCase(args[2]));
            sendChatMessage("Removed waypoint(s) with the name: " + args[2]);
        }
    }
}
