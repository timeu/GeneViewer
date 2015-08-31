package com.github.timeu.gwtlibs.geneviewer.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.timeu.gwtlibs.geneviewer.client.event.Gene;
import com.github.timeu.gwtlibs.geneviewer.client.event.ClickGeneEvent;
import com.github.timeu.gwtlibs.geneviewer.client.event.EventCallback;
import com.github.timeu.gwtlibs.geneviewer.client.event.FetchGeneEvent;
import com.github.timeu.gwtlibs.geneviewer.client.event.GeneHighlightData;
import com.github.timeu.gwtlibs.geneviewer.client.event.GeneViewerMouseMoveData;
import com.github.timeu.gwtlibs.geneviewer.client.event.HasClickGeneHandlers;
import com.github.timeu.gwtlibs.geneviewer.client.event.HasFetchGenesHandlers;
import com.github.timeu.gwtlibs.geneviewer.client.event.HasHighlightGeneHandlers;
import com.github.timeu.gwtlibs.geneviewer.client.event.HasMouseMoveHandlers;
import com.github.timeu.gwtlibs.geneviewer.client.event.HasUnhighlightGeneHandlers;
import com.github.timeu.gwtlibs.geneviewer.client.event.HasZoomResizeHandlers;
import com.github.timeu.gwtlibs.geneviewer.client.event.HighlightGeneEvent;
import com.github.timeu.gwtlibs.geneviewer.client.event.MouseMoveEvent;
import com.github.timeu.gwtlibs.geneviewer.client.event.Region;
import com.github.timeu.gwtlibs.geneviewer.client.event.UnhighlightGeneEvent;
import com.github.timeu.gwtlibs.geneviewer.client.event.ZoomResizeEvent;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.Event;


import com.github.timeu.gwtlibs.processingjsgwt.client.Processing;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RequiresResize;


public class GeneViewer extends Composite implements HasMouseMoveHandlers,HasZoomResizeHandlers,HasHandlers,
		HasFetchGenesHandlers, HasHighlightGeneHandlers,
		HasUnhighlightGeneHandlers,HasClickGeneHandlers,RequiresResize{

	interface Resources extends ClientBundle
	{
		Resources INSTANCE = GWT.create(Resources.class);
		@Source("resources/GeneViewer.pde")
		ExternalTextResource getCode();
	}

	public enum SHOW_RANGE_SELECTOR  {None,Bottom,Top};
	private int viewStart = 0;
	private int viewEnd = 0;
	private int width = 1000;
	private int height = 250;
	private String chromosome;
	private Processing<GeneViewerInstance> processing;
	private SHOW_RANGE_SELECTOR show_range_selector = SHOW_RANGE_SELECTOR.None;
	private Scheduler scheduler;
	
	private final ScheduledCommand layoutCmd = new ScheduledCommand() {
    	public void execute() {
    		layoutScheduled = false;
		    forceLayout();
		}
    };
	private boolean layoutScheduled = false;


	/**
	 * Creates a new GeneViewer widget
	 */
	public GeneViewer() {
		this(new Processing<>(), null);
	}

	// Cosntructor for testing
	GeneViewer(Processing<GeneViewerInstance> processing,Scheduler scheduler) {
		this.scheduler = scheduler;
		if (scheduler == null) {
			this.scheduler = Scheduler.get();
		}
		this.processing = processing;
		initWidget(processing);
	}

	/**
	 * Loads the ProcessingJS GeneViewer sketch.
	 * If you need to interact with the visualization wait for the execution of the onLoad runnable
	 *
	 * @param onLoad pass a callback that is executed when the visualization is finished loading
	 * @throws ResourceException
	 */
	public void load(final Runnable onLoad) throws ResourceException {
		Runnable onLoadCode = new Runnable()
		{
			@Override
			public void run() {
				if (!processing.isLoaded())
					return;
				processing.getInstance().api_setSize(width, height);
				processing.getInstance().api_setViewRegion(viewStart, viewEnd);
				processing.getInstance().api_setChromosome(chromosome);
				processing.getInstance().api_setShowRangeSelector(show_range_selector.ordinal());
				onResize();
				if (onLoad != null)
					onLoad.run();
			}
		};
		processing.load(Resources.INSTANCE.getCode(), onLoadCode);
	}

	public void resetZoom() {
		if (processing.isLoaded()) {
			processing.getInstance().api_updateZoom(viewStart, viewEnd);
		}
	}


	public void setLength(int length) {
		this.viewStart = 0;
		this.viewEnd = length;
	}
	
	public void setViewRegion(int start, int end) {
		if (this.viewStart == start && this.viewEnd == end)
			return;
		this.viewStart = start;
		this.viewEnd = end;
		if (processing.isLoaded()) {
			processing.getInstance().api_setViewRegion(start, end);
		}
	}
	

	public void setRangeSelectorPosition(SHOW_RANGE_SELECTOR show_range_selector) {
		this.show_range_selector = show_range_selector;
		if (processing.isLoaded())
			processing.getInstance().api_setShowRangeSelector(this.show_range_selector.ordinal());
	}
	
	
	public void updateZoom(Integer start,Integer end) {
		if (processing.getInstance().api_getZoomStart() == start && processing.getInstance().api_getZoomEnd() == end)
			return;
		if (processing.isLoaded()) {
			processing.getInstance().api_updateZoom(start,end);
		}
	}
	
	public void setSelectionLine(Integer position) {
		if (processing.isLoaded())
			processing.getInstance().api_setSelectionLine(position);
	}
	
	public void hideSelectionLine()	{
		if (processing.isLoaded())
			processing.getInstance().api_hideSelectionLine();
	}
	
	public void setGeneData(JsArrayMixed geneData)
	{
			assert processing.isLoaded() == true;
			processing.getInstance().api_setGeneData(geneData);
	}

	protected final void sinkEvents(Event.Type<?> type,EventHandler handler) {
		String callback = "";
		EventCallback eventCallback = null;
		if (type == MouseMoveEvent.getType()) {
			eventCallback = new EventCallback<GeneViewerMouseMoveData>() {
				@Override
				public void onCall(GeneViewerMouseMoveData data) {
					fireEvent(new MouseMoveEvent(data));
				}
			};
			callback = "mouseMoveEvent";
		}
		else if (type == ZoomResizeEvent.getType()) {
			eventCallback = new EventCallback<Region>() {
				@Override
				public void onCall(Region data) {
					fireEvent(new ZoomResizeEvent(data.getStart(),data.getEnd()));
				}
			};
			callback = "zoomResizeEvent";
		}
		else if (type == FetchGeneEvent.getType()) {
			eventCallback = new EventCallback<Region>() {
				@Override
				public void onCall(Region data) {
					fireEvent(new FetchGeneEvent(data.getStart(),data.getEnd()));
				}
			};
			callback = "fetchGeneEvent";
		}
		else if (type == HighlightGeneEvent.getType()) {
			eventCallback = new EventCallback<GeneHighlightData>() {
				@Override
				public void onCall(GeneHighlightData data) {
					fireEvent(new HighlightGeneEvent(data.getGene(),data.getX(),data.getY()));
				}
			};
			callback = "highlightGeneEvent";
		}
		else if (type == UnhighlightGeneEvent.getType()) {
			eventCallback = new EventCallback<Void>() {
				@Override
				public void onCall(Void data) {
					fireEvent(new UnhighlightGeneEvent());
				}
			};
			callback = "unhighlightGeneEvent";
		}
		else if (type == ClickGeneEvent.getType()) {
			eventCallback = new EventCallback<Gene>() {
				@Override
				public void onCall(Gene data) {
					fireEvent(new ClickGeneEvent(data));
				}
			};
			callback = "clickGeneEvent";
		}
		processing.getInstance().api_addEventHandler(callback, eventCallback);
	}

	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveEvent.Handler handler) {
		sinkEvents(MouseMoveEvent.getType(), handler);
		return addHandler(handler,MouseMoveEvent.getType());
		
	}
	
	@Override
	public HandlerRegistration addZoomResizeHandler(ZoomResizeEvent.Handler handler) {
		sinkEvents(ZoomResizeEvent.getType(), handler);
		return addHandler(handler,ZoomResizeEvent.getType());
	}


	@Override
	public HandlerRegistration addFetchGeneHandler(FetchGeneEvent.Handler handler) {
		sinkEvents(FetchGeneEvent.getType(), handler);
		return addHandler(handler,FetchGeneEvent.getType());
	}



	@Override
	public HandlerRegistration addUnhighlightGeneHandlers(
			UnhighlightGeneEvent.Handler handler) {
		sinkEvents(UnhighlightGeneEvent.getType(), handler);
		return addHandler(handler,UnhighlightGeneEvent.getType());
	}

	@Override
	public HandlerRegistration addClickGeneHandler(ClickGeneEvent.Handler handler) {
		sinkEvents(ClickGeneEvent.getType(), handler);
		return addHandler(handler, ClickGeneEvent.getType());
	}

	@Override
	public HandlerRegistration addHighlightGeneHandler(
			HighlightGeneEvent.Handler handler) {
		sinkEvents(HighlightGeneEvent.getType(), handler);
		return addHandler(handler, HighlightGeneEvent.getType());
	}

	public void redraw(boolean isFetchGenes)  {
		processing.getInstance().api_redraw(isFetchGenes);
	}

	@Override
	public void onResize() {
		scheduledLayout();
	}
	
	@Override 
	public void onAttach() {
		super.onAttach();
		onResize();
	}
	
	public void forceLayout() {
		if (!isAttached() || !isVisible())
			return;
		width = getElement().getParentElement().getClientWidth();
		height = getElement().getParentElement().getClientHeight();
		if (processing.isLoaded()) {
			processing.getInstance().api_setSize(width, height);
			processing.getInstance().api_redraw(false);
		}
	}
	
	private void scheduledLayout() {
	    if (isAttached() && !layoutScheduled) {
	      layoutScheduled = true;
	      scheduler.scheduleDeferred(layoutCmd);
	    }
	}
}
