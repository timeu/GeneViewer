package at.gmi.nordborglab.widgets.geneviewer.client.event;

import com.google.gwt.event.shared.GwtEvent;


public class ZoomResizeEvent extends GwtEvent<ZoomResizeHandler> {

	private static Type<ZoomResizeHandler> TYPE;
	
	public final int start;
	public final int stop;
	
	protected ZoomResizeEvent(int start,int stop){
		this.start = start;
		this.stop = stop;
	}
	
	public static Type<ZoomResizeHandler> getType()	{
		if (TYPE == null) {
			TYPE = new Type<ZoomResizeHandler>();
		}
		return TYPE;
	}
	
    public static void fire(HasZoomResizeHandlers source,int start,int stop)   {
    	source.fireEvent(new ZoomResizeEvent(start,stop));
    }
	
	@Override
	public Type<ZoomResizeHandler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(ZoomResizeHandler handler) {
		handler.onZoomResize(this);
	}
	
	
	
}
