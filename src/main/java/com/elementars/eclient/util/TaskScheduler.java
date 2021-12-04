package com.elementars.eclient.util;

import com.elementars.eclient.Xulu;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Elementars
 * @version Xulu v1.2.0
 * @since 7/9/2020 - 9:43 PM
 */
public class TaskScheduler {
    private final Queue<Runnable> tasks = new LinkedList<>();
    private final Queue<Runnable> prioritizedTasks = new LinkedList<>();

    int delay;

    public void onUpdate() {
        if (delay > 0) delay--;
        if (prioritizedTasks.peek() != null) {
            prioritizedTasks.remove().run();
            delay = Xulu.VALUE_MANAGER.<Integer>getValueByName("Offhand Delay").getValue();
            return;
        }
        if (tasks.peek() != null && delay == 0) {
            tasks.remove().run();
            delay = Xulu.VALUE_MANAGER.<Integer>getValueByName("Offhand Delay").getValue();
        }
    }

    public void addTask(Runnable r) {
        tasks.add(r);
    }

    public void addPrioritizedTask(Runnable r) {
        prioritizedTasks.add(r);
    }
}
