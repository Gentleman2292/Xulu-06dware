package com.elementars.eclient.event;

import java.lang.reflect.InvocationTargetException;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.util.Wrapper;

public abstract class Event {

	private State state = State.PRE;
	private boolean cancelled;
	private final float partialTicks;

	public Event() {
		this.partialTicks = Wrapper.getMinecraft().getRenderPartialTicks();
	}

	public float getPartialTicks() {
		return partialTicks;
	}

	public State getEventState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public enum State {
		PRE, POST
	}

	public Event call() {
		this.cancelled = false;
		this.call(this);
		return this;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean cancelled) {

		this.cancelled = cancelled;
	}

	private static void call(Event event) {
		ArrayHelper<Data> dataList = Xulu.EVENT_MANAGER.get(event.getClass());
		if (dataList != null) {
			for (Data data : dataList) {
				try {
					data.target.invoke(data.source, event);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}

			}
		}
	}
}
