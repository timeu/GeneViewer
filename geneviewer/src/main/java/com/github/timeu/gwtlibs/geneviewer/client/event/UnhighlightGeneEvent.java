package com.github.timeu.gwtlibs.geneviewer.client.event;


import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


public class UnhighlightGeneEvent extends GwtEvent<UnhighlightGeneEvent.Handler> {

	public interface Handler extends EventHandler {

		void onUnhighlightGene(UnhighlightGeneEvent event);
	}


	private static Type<Handler> TYPE;
	
	public UnhighlightGeneEvent(){
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
		handler.onUnhighlightGene(this);
	}
}
