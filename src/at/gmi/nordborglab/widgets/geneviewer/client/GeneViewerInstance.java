package at.gmi.nordborglab.widgets.geneviewer.client;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HasHandlers;
import com.processingjs.client.ProcessingInstance;

public class GeneViewerInstance extends ProcessingInstance{
	
	protected GeneViewerInstance() {}
	
	protected final native void test() /*-{
		alert('test');
	}-*/;

	public final native void setLength(int length) /*-{
		this.api_setLength(length);
	}-*/;
	
	public final native void updateZoom(int start,int end) /*-{
		this.api_updateZoom(start,end);
	}-*/;
	
	public final native int getZoomStart() /*-{
		return this.api_getZoomStart();
	}-*/;
	
	public final native int getZoomEnd() /*-{
		return this.api_getZoomEnd();
	}-*/;
	
	public final native void setMaximumZoomToDraw(int maximumZoomToDraw) /*-{
		this.api_setMaximumZoomToDraw(maximumZoomToDraw);
	}-*/;
	
	public final native int getMaximumZoomToDraw() /*-{
		return this.api_getMaximumZoomToDraw();
	}-*/;
	
	public final native void setMaximumZoomToDrawFeatures(int maximumZoomToDrawFeatures) /*-{
		this.api_setMaximumZoomToDrawFeatures(maximumZoomToDrawFeatures);
	}-*/;
	
	public final native int getMaximumZoomToDrawFeatures() /*-{
		return this.api_getMaximumZoomToDrawFeatures();
	}-*/;
	
	public final native void setSelectionLine(int position) /*-{
		this.api_setSelectionLine(position);
	}-*/;
	
	public final native void setSelectionLinePx(int position_px) /*-{
	   this.api_setSelectionLinePx(position_px);
	}-*/;
	
	
	public final native void sinkNativeEvent(HasHandlers view, String callback) /*-{
		if (callback == 'mouseMoveEvent')
		{
			var callback_func = function(position,gene) {
				var geneData = @at.gmi.nordborglab.widgets.geneviewer.client.GeneViewerMouseMoveData::new(ILjava/lang/String;)(position,gene);
			    @at.gmi.nordborglab.widgets.geneviewer.client.event.MouseMoveEvent::fire(Lat/gmi/nordborglab/widgets/geneviewer/client/event/HasMouseMoveHandlers;Lat/gmi/nordborglab/widgets/geneviewer/client/GeneViewerMouseMoveData;)(view,geneData);
			};
		}
		else if (callback == 'zoomResizeEvent')
		{
			var callback_func = function(start,end) {
			    @at.gmi.nordborglab.widgets.geneviewer.client.event.ZoomResizeEvent::fire(Lat/gmi/nordborglab/widgets/geneviewer/client/event/HasZoomResizeHandlers;II)(view,start,end);
			};
		}
		else if (callback == 'fetchGeneEvent')
		{
			var callback_func = function(start,end) {
			    @at.gmi.nordborglab.widgets.geneviewer.client.event.FetchGeneEvent::fire(Lat/gmi/nordborglab/widgets/geneviewer/client/event/HasFetchGenesHandlers;II)(view,start,end);
			};
		}
		this.api_addEventHandler(callback,callback_func);
	}-*/;
	
	public final native void clearNativeEvents() /*-{
		this.api_clearEventHandlers();
	}-*/;

	public final native void hideSelectionLine() /*-{
		this.api_hideSelectionLine();
	}-*/;

	public final native void setGeneData(JavaScriptObject geneData) /*-{
		this.api_setGeneData(geneData);		
	}-*/;
}
