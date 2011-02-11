package at.gmi.nordborglab.widgets.geneviewer.client.event;

import at.gmi.nordborglab.widgets.geneviewer.client.GeneViewerMouseMoveData;


import com.google.gwt.event.shared.GwtEvent;


public class MouseMoveEvent extends GwtEvent<MouseMoveHandler> {

	private static Type<MouseMoveHandler> TYPE;
	
	protected GeneViewerMouseMoveData data;
	
	protected MouseMoveEvent(GeneViewerMouseMoveData data){
		this.data = data;
	}
	
	public static Type<MouseMoveHandler> getType()	{
		if (TYPE == null) {
			TYPE = new Type<MouseMoveHandler>();
		}
		return TYPE;
	}
	
    public static void fire(HasMouseMoveHandlers source,GeneViewerMouseMoveData data)   {
    	source.fireEvent(new MouseMoveEvent(data));
    }
	
	@Override
	public Type<MouseMoveHandler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(MouseMoveHandler handler) {
		handler.onMoveMove(this);
	}
	
	
}
