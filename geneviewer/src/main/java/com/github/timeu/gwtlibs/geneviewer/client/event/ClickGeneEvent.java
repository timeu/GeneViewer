package com.github.timeu.gwtlibs.geneviewer.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ClickGeneEvent extends GwtEvent<ClickGeneEvent.Handler> {

	public interface Handler extends EventHandler {

		void onClickGene(ClickGeneEvent event);
	}

	private static Type<ClickGeneEvent.Handler> TYPE;
	
	protected final Gene gene;
	
	public ClickGeneEvent(Gene gene){
		this.gene = gene;
	}
	
	public static Type<ClickGeneEvent.Handler> getType()	{
		if (TYPE == null) {
			TYPE = new Type<>();
		}
		return TYPE;
	}
	
	@Override
	public Type<ClickGeneEvent.Handler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onClickGene(this);
	}
	
	public Gene getGene() 
	{
		return gene;
	}
}