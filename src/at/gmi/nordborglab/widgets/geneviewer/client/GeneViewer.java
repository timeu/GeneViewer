package at.gmi.nordborglab.widgets.geneviewer.client;

import java.util.ArrayList;
import java.util.HashMap;

import at.gmi.nordborglab.widgets.geneviewer.client.datasource.DataSource;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.DataSourceCallback;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.FetchGeneDescriptionCallback;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.Gene;
import at.gmi.nordborglab.widgets.geneviewer.client.event.ClickGeneEvent;
import at.gmi.nordborglab.widgets.geneviewer.client.event.ClickGeneHandler;
import at.gmi.nordborglab.widgets.geneviewer.client.event.FetchGeneEvent;
import at.gmi.nordborglab.widgets.geneviewer.client.event.FetchGeneHandler;
import at.gmi.nordborglab.widgets.geneviewer.client.event.HasClickGeneHandlers;
import at.gmi.nordborglab.widgets.geneviewer.client.event.HasFetchGenesHandlers;
import at.gmi.nordborglab.widgets.geneviewer.client.event.HasHighlightGeneHandlers;
import at.gmi.nordborglab.widgets.geneviewer.client.event.HasMouseMoveHandlers;
import at.gmi.nordborglab.widgets.geneviewer.client.event.HasUnhighlightGeneHandlers;
import at.gmi.nordborglab.widgets.geneviewer.client.event.HasZoomResizeHandlers;
import at.gmi.nordborglab.widgets.geneviewer.client.event.HighlightGeneEvent;
import at.gmi.nordborglab.widgets.geneviewer.client.event.HighlightGeneHandler;
import at.gmi.nordborglab.widgets.geneviewer.client.event.MouseMoveEvent;
import at.gmi.nordborglab.widgets.geneviewer.client.event.MouseMoveHandler;
import at.gmi.nordborglab.widgets.geneviewer.client.event.UnhighlightGeneEvent;
import at.gmi.nordborglab.widgets.geneviewer.client.event.UnhighlightGeneHandler;
import at.gmi.nordborglab.widgets.geneviewer.client.event.ZoomResizeEvent;
import at.gmi.nordborglab.widgets.geneviewer.client.event.ZoomResizeHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.logical.shared.HighlightEvent;
import com.google.gwt.event.logical.shared.HighlightHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.Widget;
import com.processingjs.client.Processing;


public class GeneViewer extends Composite implements HasMouseMoveHandlers, HasZoomResizeHandlers,HasHandlers, 
								HasFetchGenesHandlers,FetchGeneHandler, HasHighlightGeneHandlers, 
								HasUnhighlightGeneHandlers,HasClickGeneHandlers,HighlightGeneHandler,UnhighlightGeneHandler{
	
	interface Resources extends ClientBundle
	{
		Resources INSTANCE = GWT.create(Resources.class);
		@Source("GeneViewer.pde")
		ExternalTextResource getCode();
	}
	
	private static GeneViewerUiBinder uiBinder = GWT
			.create(GeneViewerUiBinder.class);

	interface GeneViewerUiBinder extends UiBinder<Widget, GeneViewer> {
	}
	
	protected boolean fetchGenes = true;
	protected int length = 30000;
	protected String chromosome;
	protected DataSource datasource;
	protected boolean isShowDescription = true;
	
	protected HashMap<String, String> descriptions = new HashMap<String, String>();

	@UiField Processing<GeneViewerInstance> processing;
	@UiField FlowPanel container;
	@UiField PopupPanel description_popup;
	@UiField Label description_label;
	@UiField Label position_label;
	//@UiField Label chromosome_label;
	@UiField SpanElement name_label;
	
	
	public GeneViewer() {
		initWidget(uiBinder.createAndBindUi(this));
		description_popup.removeFromParent();
	}

	
	public void setSize(Integer width,Integer height)
	{
		Element elem = processing.getElement();
		DOM.setElementAttribute(elem, "width", width.toString());
		DOM.setElementAttribute(elem, "height", height.toString());
	}
	
	public void setDataSource(DataSource datasource) {
		this.datasource = datasource;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public void setIsShowDescription(boolean isShowDescription) {
		this.isShowDescription = isShowDescription; 
	}
	
	
	
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	
	public void updateZoom(Integer start,Integer end) {
		if (processing.isLoaded())
			processing.getInstance().updateZoom(start,end);
	}
	
	public void setSelectionLine(Integer position) {
		if (processing.isLoaded())
			processing.getInstance().setSelectionLine(position);
	}
	
	public void hideSelectionLine()	{
		if (processing.isLoaded())
			processing.getInstance().hideSelectionLine();
	}
	
	public void setGeneData(JavaScriptObject geneData)
	{
			assert processing.isLoaded() == true;
			processing.getInstance().setGeneData(geneData);
	}
	
	public void load(final Runnable onLoad) throws ResourceException {
		Runnable onLoadCode = new Runnable() 
		{
			@Override
			public void run() {
				if (!processing.isLoaded())
					return;
				processing.getInstance().setLength(length);
				processing.getInstance().setChromosome(chromosome);
				addFetchGeneHandler(GeneViewer.this);
				if (isShowDescription) {
					addHighlightGeneHandler(GeneViewer.this);
					addUnhighlightGeneHandlers(GeneViewer.this);
				}
				if (onLoad != null)
					onLoad.run();
			}
		};
		processing.load(Resources.INSTANCE.getCode(), onLoadCode);
	}


	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		sinkEvents(MouseMoveEvent.getType());
		return addHandler(handler,MouseMoveEvent.getType());
		
	}
	
	
	
	protected final void sinkEvents(
		      GwtEvent.Type<?> type) {
		String callback = "";
		if (type == MouseMoveEvent.getType())
			callback = "mouseMoveEvent";
		else if (type == ZoomResizeEvent.getType())
			callback = "zoomResizeEvent";
		else if (type == FetchGeneEvent.getType())
			callback = "fetchGeneEvent";
		else if (type == HighlightGeneEvent.getType())
			callback = "highlightGeneEvent";
		else if (type == UnhighlightGeneEvent.getType())
			callback = "unhighlightGeneEvent";
		else if (type == ClickGeneEvent.getType())
			callback = "clickGeneEvent";
		processing.getInstance().sinkNativeEvent(this,callback);
		
	}


	@Override
	public HandlerRegistration addZoomResizeHandler(ZoomResizeHandler handler) {
		sinkEvents(ZoomResizeEvent.getType());
		return addHandler(handler,ZoomResizeEvent.getType());
	}


	@Override
	public HandlerRegistration addFetchGeneHandler(FetchGeneHandler handler) {
		sinkEvents(FetchGeneEvent.getType());
		return addHandler(handler,FetchGeneEvent.getType());
	}


	@Override
	public void onFetchGenes(FetchGeneEvent event) {

		if (chromosome == null || chromosome.isEmpty() || this.datasource == null) 
			return;
		
		if (descriptions.size() == 100)
			descriptions.clear();
		
		boolean getFeatures = true;
		int maximumZoomToDrawFeatures = processing.getInstance().getMaximumZoomToDrawFeatures();
		int zoomStart = processing.getInstance().getZoomStart();
		int zoomEnd = processing.getInstance().getZoomEnd();
		if (maximumZoomToDrawFeatures != -1 && maximumZoomToDrawFeatures<= (zoomEnd-zoomStart))
			getFeatures = false;
		datasource.fetchGenes(chromosome, zoomStart, zoomEnd, getFeatures, new DataSourceCallback() {

			@Override
			public void onFetchGenes(JavaScriptObject genes) {
				processing.getInstance().setGeneData(genes);
			} 
		});
	}



	@Override
	public HandlerRegistration addUnhighlightGeneHandlers(
			UnhighlightGeneHandler handler) {
		sinkEvents(UnhighlightGeneEvent.getType());
		return addHandler(handler,UnhighlightGeneEvent.getType());
	}


	
	@Override
	public HandlerRegistration addClickGeneHandler(ClickGeneHandler handler) {
		sinkEvents(ClickGeneEvent.getType());
		return addHandler(handler, ClickGeneEvent.getType());
	}





	@Override
	public void onHightlightGene(final HighlightGeneEvent event) {
		if (descriptions.containsKey(event.getGene().getName()))
		{
			event.getGene().setDescription(descriptions.get(event.getGene().getName()));
			showGeneDescriptionPopup(event.getGene(),event.getX(), event.getY());
		}
		else if (event.getGene().getDescription().isEmpty()) 
		{
			datasource.fetchGeneDescription(event.getGene().getName(),new FetchGeneDescriptionCallback() {
				
				@Override
				public void onFetchGeneDescription(String description) {
					descriptions.put(event.getGene().getName(),description);
					event.getGene().setDescription(descriptions.get(event.getGene().getName()));
					showGeneDescriptionPopup(event.getGene(), event.getX(), event.getY());
				}
			});
		}
		else {
			showGeneDescriptionPopup(event.getGene(), event.getX(), event.getY());
		}
			
	}
	
	protected void showGeneDescriptionPopup(Gene gene,int x, int y) {
		description_label.setText(gene.getDescription());
		name_label.setInnerText(gene.getName());
		position_label.setText(gene.getStart() + " - " + gene.getEnd() + " (" + gene.getChromosome() + ")");
		//chromosome_label.setText(gene.getChromosome());
		//x += this.getAbsoluteLeft();
		y += this.getAbsoluteTop();
		description_popup.setPopupPosition(x,y);
		description_popup.show();
	}


	@Override
	public HandlerRegistration addHighlightGeneHandler(
			HighlightGeneHandler handler) {
		sinkEvents(HighlightGeneEvent.getType());
		return addHandler(handler, HighlightGeneEvent.getType());
	}


	@Override
	public void onUnhighlightGene(UnhighlightGeneEvent event) {
		description_popup.hide();
	}
}
