package com.elementars.eclient.command.commands;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.enemy.Enemy;
import com.elementars.eclient.enemy.Enemies;

public class EnemyCommand extends Command {
    public EnemyCommand() {
        super("enemy", "adds or deletes enemies", new String[]{"add", "del"});
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            sendChatMessage("Try .enemy add or .enemy del");
            return;
        }
        if (args[1].equalsIgnoreCase("help")) {
            showSyntax(args[0]);
        }
        if (args.length < 3) {
            sendChatMessage("Specify a username");
            return;
        }
        Enemy enemy = new Enemy(args[2]);
        if (args[1].equalsIgnoreCase("add")) {
            if (!Enemies.getEnemies().contains(enemy)) {
                Enemies.addEnemy(enemy.getUsername());
                sendChatMessage(enemy.getUsername() + " is now an enemy");
            }else{
                sendChatMessage(enemy.getUsername() + " is already an enemy!");
            }
        }
        else if (args[1].equalsIgnoreCase("del")) {
            if (Enemies.getEnemyByName(enemy.getUsername()) != null) {
                Enemies.delEnemy(enemy.getUsername());
                sendChatMessage(enemy.getUsername() + " is no longer an enemy");
            }else{
                sendChatMessage(enemy.getUsername() + " isn't an enemy");
            }
        }
        else {
            sendChatMessage("Unknown attribute '" + args[1] + "'");
        }
    }
}
