package at.gmi.nordborglab.widgets.geneviewer.client.event;

import at.gmi.nordborglab.widgets.geneviewer.client.datasource.Gene;

import com.google.gwt.event.logical.shared.HasHighlightHandlers;
import com.google.gwt.event.logical.shared.HighlightEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

public class HighlightGeneEvent extends GwtEvent<HighlightGeneHandler> {

	private static Type<HighlightGeneHandler> TYPE;
	
	protected Gene gene;
	protected int x;
	protected int y;
	
	protected HighlightGeneEvent(Gene gene,int x, int y){
		this.gene = gene;
		this.x = x;
		this.y = y;
	}
	
	public static Type<HighlightGeneHandler> getType()	{
		if (TYPE == null) {
			TYPE = new Type<HighlightGeneHandler>();
		}
		return TYPE;
	}
	
    public static void fire(HasHighlightGeneHandlers source,Gene gene,int x, int y)   {
    	source.fireEvent(new HighlightGeneEvent(gene,x,y));
    }
	
	@Override
	public Type<HighlightGeneHandler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(HighlightGeneHandler handler) {
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
