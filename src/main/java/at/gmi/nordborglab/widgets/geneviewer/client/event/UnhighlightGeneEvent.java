package at.gmi.nordborglab.widgets.geneviewer.client.event;


import com.google.gwt.event.shared.GwtEvent;


public class UnhighlightGeneEvent extends GwtEvent<UnhighlightGeneHandler> {

	private static Type<UnhighlightGeneHandler> TYPE;
	
	protected UnhighlightGeneEvent(){
	}
	
	public static Type<UnhighlightGeneHandler> getType()	{
		if (TYPE == null) {
			TYPE = new Type<UnhighlightGeneHandler>();
		}
		return TYPE;
	}
	
    public static void fire(HasUnhighlightGeneHandlers source)   {
    	source.fireEvent(new UnhighlightGeneEvent());
    }
	
	@Override
	public Type<UnhighlightGeneHandler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(UnhighlightGeneHandler handler) {
		handler.onUnhighlightGene(this);
	}
}
