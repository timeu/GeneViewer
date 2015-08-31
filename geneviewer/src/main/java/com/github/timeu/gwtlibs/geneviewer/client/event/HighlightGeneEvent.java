package com.github.timeu.gwtlibs.geneviewer.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class HighlightGeneEvent extends GwtEvent<HighlightGeneEvent.Handler> {

	public interface Handler  extends EventHandler {
		void onHightlightGene(HighlightGeneEvent event);
	}

	private static Type<Handler> TYPE;
	
	protected Gene gene;
	protected int x;
	protected int y;
	
	public HighlightGeneEvent(Gene gene,int x, int y){
		this.gene = gene;
		this.x = x;
		this.y = y;
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
		handler.onHightlightGene(this);
	}
	
	public Gene getGene() 
	{
		return gene;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() { 
		return y;
	}
}
