package com.github.timeu.gwtlibs.geneviewer.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class FetchGeneEvent extends GwtEvent<FetchGeneEvent.Handler> {

	public interface Handler extends EventHandler {

		void onFetchGenes(FetchGeneEvent event);
	}

	private static Type<Handler> TYPE;
	
	public final  int start;
	public final int end;
	
	public FetchGeneEvent(int start, int end){
		this.start = start;
		this.end = end;
	}
	
	public static Type<Handler> getType()	{
		if (TYPE == null) {
			TYPE = new Type<>();
		}
		return TYPE;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	@Override
	public Type<Handler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onFetchGenes(this);
	}
	
	
}

