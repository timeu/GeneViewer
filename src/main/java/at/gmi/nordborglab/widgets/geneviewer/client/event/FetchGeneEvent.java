package at.gmi.nordborglab.widgets.geneviewer.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class FetchGeneEvent extends GwtEvent<FetchGeneHandler> {

	private static Type<FetchGeneHandler> TYPE;
	
	public final  int start;
	public final int end;
	
	protected FetchGeneEvent(int start, int end){
		this.start = start;
		this.end = end;
	}
	
	public static Type<FetchGeneHandler> getType()	{
		if (TYPE == null) {
			TYPE = new Type<FetchGeneHandler>();
		}
		return TYPE;
	}
	
    public static void fire(HasFetchGenesHandlers source,int start,int end)   {
    	source.fireEvent(new FetchGeneEvent(start,end));
    }
	
	@Override
	public Type<FetchGeneHandler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(FetchGeneHandler handler) {
		handler.onFetchGenes(this);
	}
	
	
}

