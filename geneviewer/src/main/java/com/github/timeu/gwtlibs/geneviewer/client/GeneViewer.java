package com.github.timeu.gwtlibs.geneviewer.client;


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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.user.client.ui.Composite;
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


	public enum SHOW_RANGE_SELECTOR  {None,Bottom,Top;};
    private int viewStart = 0;
    private int viewEnd = 0;
    private int width = 1000;
    private int height = 250;
    private String chromosome;
    private boolean showGeneInfoPopup = true;
    private GeneInfoPopup geneInfoPopup = new GeneInfoPopup();
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
        geneInfoPopup.removeFromParent();
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
				sinkEvents();
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

	public int getMaximumZoomToDrawFeatures() {
		return processing.getInstance().api_getMaximumZoomToDrawFeatures();
	}

	public boolean shouldDrawFeatures() {
		int maximumZoom = getMaximumZoomToDrawFeatures();
		int currentZoom = (viewEnd - viewEnd);
		return !(maximumZoom != -1 && maximumZoom <= currentZoom);
	}

	private final void sinkEvents() {
		sinkEvent(new EventCallback<GeneViewerMouseMoveData>() {
			@Override
			public void onCall(GeneViewerMouseMoveData data) {
				fireEvent(new MouseMoveEvent(data));
			}
		},"mouseMoveEvent");


		sinkEvent(new EventCallback<Region>() {
			@Override
			public void onCall(Region data) {
				fireEvent(new ZoomResizeEvent(data.start,data.end));
			}
		},"zoomResizeEvent");

		sinkEvent(new EventCallback<Region>() {
			@Override
			public void onCall(Region data) {
				fireEvent(new FetchGeneEvent(data.start,data.end));
			}
		},"fetchGeneEvent");

		sinkEvent(new EventCallback<GeneHighlightData>() {
			@Override
			public void onCall(GeneHighlightData data) {
				fireEvent(new HighlightGeneEvent(data.gene,data.x,data.y));
				showGeneInfoPopup(data);
			}
		},"highlightGeneEvent");

		sinkEvent(new EventCallback<Void>() {
			@Override
			public void onCall(Void data) {
				fireEvent(new UnhighlightGeneEvent());
				if (showGeneInfoPopup) {
					geneInfoPopup.hide();
				}
			}
		},"unhighlightGeneEvent");

		sinkEvent(new EventCallback<Gene>() {
			@Override
			public void onCall(Gene data) {
				fireEvent(new ClickGeneEvent(data));
			}
		},"clickGeneEvent");
	}

	private final void sinkEvent(EventCallback eventCallback,String callback)  {
		processing.getInstance().api_addEventHandler(callback, eventCallback);
	}

    private void showGeneInfoPopup(GeneHighlightData data) {
        if (!showGeneInfoPopup)
            return;
        if (chromosome != null && !chromosome.isEmpty() &&
                (data.gene.chromosome == null || data.gene.chromosome.isEmpty()) ) {
            data.gene.chromosome = chromosome;
        }
        geneInfoPopup.setGene(data.gene);
        int y = data.y;
        int x = data.x;
        y += this.getAbsoluteTop();
        geneInfoPopup.setPopupPosition(x,y);
        geneInfoPopup.show();

    }

    @Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveEvent.Handler handler) {
		return addHandler(handler,MouseMoveEvent.getType());

	}

	@Override
	public HandlerRegistration addZoomResizeHandler(ZoomResizeEvent.Handler handler) {
		return addHandler(handler,ZoomResizeEvent.getType());
	}

	@Override
	public HandlerRegistration addFetchGeneHandler(FetchGeneEvent.Handler handler) {
		return addHandler(handler,FetchGeneEvent.getType());
	}


	@Override
	public HandlerRegistration addUnhighlightGeneHandlers(
			UnhighlightGeneEvent.Handler handler) {
		return addHandler(handler, UnhighlightGeneEvent.getType());
	}



	@Override
	public HandlerRegistration addClickGeneHandler(ClickGeneEvent.Handler handler) {
		return addHandler(handler, ClickGeneEvent.getType());
	}

	@Override
	public HandlerRegistration addHighlightGeneHandler(
			HighlightGeneEvent.Handler handler) {
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

	@Override
	protected void onDetach() {
		geneInfoPopup.hide();
		super.onDetach();
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

    public void setGeneInfo(String info) {
        geneInfoPopup.setGeneInfo(info);
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }
}
