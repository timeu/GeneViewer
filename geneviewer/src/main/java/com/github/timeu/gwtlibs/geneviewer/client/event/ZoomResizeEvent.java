package com.github.timeu.gwtlibs.geneviewer.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


public class ZoomResizeEvent extends GwtEvent<ZoomResizeEvent.Handler> {

	public interface Handler extends EventHandler {
		void onZoomResize(ZoomResizeEvent event);
	}

	private static Type<Handler> TYPE;
	
	public final int start;
	public final int stop;
	
	public ZoomResizeEvent(int start,int stop){
		this.start = start;
		this.stop = stop;
	}
	
	public static Type<Handler> getType()	{
		if (TYPE == null) {
			TYPE = new Type<>();
		}
		return TYPE;
	}
	
	@Override
	public Type<Handler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onZoomResize(this);
	}
	
	
	
}
