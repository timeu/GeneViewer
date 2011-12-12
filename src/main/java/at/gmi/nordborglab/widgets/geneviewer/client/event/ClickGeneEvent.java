package at.gmi.nordborglab.widgets.geneviewer.client.event;

import at.gmi.nordborglab.widgets.geneviewer.client.datasource.Gene;
import com.google.gwt.event.shared.GwtEvent;

public class ClickGeneEvent extends GwtEvent<ClickGeneHandler> {

	private static Type<ClickGeneHandler> TYPE;
	
	protected Gene gene;
	
	protected ClickGeneEvent(Gene gene){
		this.gene = gene;
	}
	
	public static Type<ClickGeneHandler> getType()	{
		if (TYPE == null) {
			TYPE = new Type<ClickGeneHandler>();
		}
		return TYPE;
	}
	
    public static void fire(HasClickGeneHandlers source,Gene gene)   {
    	source.fireEvent(new ClickGeneEvent(gene));
    }
	
	@Override
	public Type<ClickGeneHandler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(ClickGeneHandler handler) {
		handler.onClickGene(this);
	}
	
	public Gene getGene() 
	{
		return gene;
	}
}