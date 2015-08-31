package com.github.timeu.gwtlibs.geneviewer.client.event;



import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


public class MouseMoveEvent extends GwtEvent<MouseMoveEvent.Handler> {

	public interface Handler extends EventHandler {

		void onMoveMove(MouseMoveEvent event);
	}

	private static Type<Handler> TYPE;
	
	protected GeneViewerMouseMoveData data;
	
	public MouseMoveEvent(GeneViewerMouseMoveData data){
		this.data = data;
	}
	
	public static Type<Handler> getType()	{
		if (TYPE == null) {
			TYPE = new Type<>();
		}
		return TYPE;
	}

	public GeneViewerMouseMoveData getData() {
		return data;
	}

	@Override
	public Type<Handler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onMoveMove(this);
	}
}
