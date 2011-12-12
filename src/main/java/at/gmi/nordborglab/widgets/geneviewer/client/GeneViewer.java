package at.gmi.nordborglab.widgets.geneviewer.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.danvk.dygraphs.client.Dygraphs;
import org.danvk.dygraphs.client.Dygraphs.Options.SHOW_LEGEND;
import org.danvk.dygraphs.client.events.UnderlayHandler;

import at.gmi.nordborglab.processingjs.client.Processing;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.DataSource;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.DataSourceCallback;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.FetchGeneDescriptionCallback;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.FetchGenomeStatsDataCallback;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.FetchGenomeStatsListCallback;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.Gene;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.GenomeStat;
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
import at.gmi.nordborglab.widgets.geneviewer.client.resources.MyResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;


public class GeneViewer extends Composite implements HasMouseMoveHandlers, HasZoomResizeHandlers,HasHandlers, 
								HasFetchGenesHandlers,FetchGeneHandler, HasHighlightGeneHandlers, 
								HasUnhighlightGeneHandlers,HasClickGeneHandlers,HighlightGeneHandler,UnhighlightGeneHandler,ClickGeneHandler,RequiresResize{
	
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
	
	public enum SHOW_RANGE_SELECTOR  {None,Bottom,Top};
	
	protected boolean fetchGenes = true;
	protected boolean showStatsBand = true;
	protected int viewStart = 0;
	protected int viewEnd = 0;
	protected int width = 1000;
	protected int height = 250;
	protected int statsBandHeight = 100; 
	protected boolean isGenomeStatsDrawn = false;
	protected String chromosome;
	protected DataSource datasource;
	protected boolean isShowDescription = true;
	protected HashMap<String, String> descriptions = new HashMap<String, String>();
	protected String geneInfoUrl = null;
	protected SHOW_RANGE_SELECTOR show_range_selector = SHOW_RANGE_SELECTOR.None;
	protected List<GenomeStat> genomeStats;
	protected List<GenomeStat> currentGenomeStats;
	protected DataTable stackableGenomeStatsCache = null;
	protected HashMap<GenomeStat,DataTable> nonstackableGenomeStatsCache = new HashMap<GenomeStat,DataTable>();
	protected Dygraphs.Options options = Dygraphs.Options.create();
	protected DataTable statisticsDataTable;
	protected int width_offset = 31;
	
	private final ScheduledCommand layoutCmd = new ScheduledCommand() {
    	public void execute() {
    		layoutScheduled = false;
		    forceLayout();
		}
    };
	private boolean layoutScheduled = false;

	@UiField Processing<GeneViewerInstance> processing;
	@UiField Dygraphs genomeStatChart;
	@UiField PopupPanel description_popup;
	@UiField PopupPanel settings_popup;
	@UiField Label description_label;
	@UiField Label position_label;
	@UiField ToggleButton settings_btn;
	@UiField FlowPanel container;
	//@UiField Label chromosome_label;
	@UiField HTMLPanel track_container;
	@UiField SpanElement name_label;
	@UiField(provided=true) MyResources mainRes;
	List<HandlerRegistration> handlerRegistrations = new ArrayList<HandlerRegistration>();
	
	
	public GeneViewer() {
		mainRes = GWT.create(MyResources.class);
		mainRes.style().ensureInjected();
		initWidget(uiBinder.createAndBindUi(this));
		description_popup.removeFromParent();
		settings_popup.removeFromParent();
		settings_popup.setAnimationEnabled(true);
		initStatisticsChart();
		settings_btn.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (!event.getValue())
					settings_popup.hide();
				else
					settings_popup.showRelativeTo(settings_btn);
			}
		});
	}
	

	public void setWidthOffset(int offset) {
		this.width_offset = offset;
	}
	
	/*public void setSize(Integer width,Integer height)
	{
		if (!dynamicWidth) {
			this.width= width;
		}
		this.height = height - (showStatsBand ? statsBandHeight : 0);
	}*/
	
	public void setDataSource(DataSource datasource) {
		this.datasource = datasource;
		loadGenomeStats();
	}
	
	public void setShowStatsBand(boolean showStatsBand) {
		this.showStatsBand = showStatsBand;
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
			processing.getInstance().setViewRegion(start, end);
		}
	}
	
	public void setIsShowDescription(boolean isShowDescription) {
		this.isShowDescription = isShowDescription; 
	}
	
	public void setGeneInfoUrl(String geneInfoUrl) {
		this.geneInfoUrl = geneInfoUrl;
	}
	
	public void setRangeSelectorPosition(SHOW_RANGE_SELECTOR show_range_selector) {
		this.show_range_selector = show_range_selector;
		if (processing.isLoaded())
			processing.getInstance().showRangeSelector(this.show_range_selector.ordinal());
	}
	
	
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	
	public void updateZoom(Integer start,Integer end) {
		if (processing.getInstance().getZoomStart() == start && processing.getInstance().getZoomEnd() == end)
			return;
		if (processing.isLoaded()) {
			processing.getInstance().updateZoom(start,end);
			if (showStatsBand)
				drawStatistics();
		}
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
				processing.getInstance().setLayoutSize(width,height);
				processing.getInstance().setViewRegion(viewStart,viewEnd);
				processing.getInstance().setChromosome(chromosome);
				processing.getInstance().showRangeSelector(show_range_selector.ordinal());
				addFetchGeneHandler(GeneViewer.this);
				if (isShowDescription) {
					addHighlightGeneHandler(GeneViewer.this);
					addUnhighlightGeneHandlers(GeneViewer.this);
				}
				if (geneInfoUrl != null)
				{
					addClickGeneHandler(GeneViewer.this);
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
		position_label.setText(gene.getStart() + " - " + gene.getEnd() + " (" + chromosome + ")");
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
	
	public void redraw(boolean isFetchGenes)  {
		processing.getInstance().redraw(isFetchGenes);
	}


	@Override
	public void onClickGene(ClickGeneEvent event) {
		if (geneInfoUrl != null && !geneInfoUrl.isEmpty())
			Window.open(geneInfoUrl.replace("{0}", event.getGene().getName()),"",""); 
	}
	
	
	
	
	private void loadGenomeStats() {
		if (datasource == null || !showStatsBand)
			return;
		datasource.fetchGenomeStatsList(new FetchGenomeStatsListCallback() {
			
			@Override
			public void onFetchGenomeStatsList(List<GenomeStat> stats) {
				genomeStats = stats;
				initSettingsPopup();
				List<GenomeStat> stats_to_load = new ArrayList<GenomeStat>();
				stats_to_load.add(genomeStats.get(0));
				currentGenomeStats = stats_to_load;
				loadAndDisplayGenomeStats(true);
			}
		});
	}
	
	private void loadAndDisplayGenomeStats(final boolean isDisplay) {
		if (datasource == null)
			return;
		
		updateSettingChecked();
		List<GenomeStat>stats_to_fetch = getMissingGenomeStats();
		if (stats_to_fetch != null) {
			datasource.fetchGenomeStatsData(stats_to_fetch,chromosome, new FetchGenomeStatsDataCallback() {
				
				@Override
				public void onFetchGenomeStats(DataTable data) {
					cacheDataTable(data); 
					drawStatistics();
				}
			} );
		}
		else
		{
			drawStatistics();
		}
	}

	private void initSettingsPopup() {
		ClickHandler image_click_handler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Image source = (Image)event.getSource();
				String track = source.getAltText();
				GenomeStat currentStat = GenomeStat.getFromName(currentGenomeStats, track);
				// At least 1 GenomeStat must be selected
				if (currentStat != null && currentGenomeStats.size() == 1) 
					return;
				
				GenomeStat stackToModify = GenomeStat.getFromName(genomeStats, track);
				
				// if stackToModify is not stackable or is stackable but there is already a non stackable genomestat dispalyed, reset currentGenomeStat list and ad item
				if (!stackToModify.isStackable() || (currentGenomeStats.size() == 1 && !currentGenomeStats.get(0).isStackable())) {
					currentGenomeStats.clear();
					currentGenomeStats.add(stackToModify);
				}
				else
				{
					if (currentStat == null) 
						currentGenomeStats.add(stackToModify);
					else
						currentGenomeStats.remove(stackToModify);
				}
				loadAndDisplayGenomeStats(true);
			}
		};
		
		ClickHandler text_click_handler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Anchor source = (Anchor)event.getSource();
				String track = source.getName();
				GenomeStat stackToModify = GenomeStat.getFromName(genomeStats, track);
				currentGenomeStats.clear();
				currentGenomeStats.add(stackToModify);
				loadAndDisplayGenomeStats(true);
			}
		};
		track_container.clear();
		for (GenomeStat stat:genomeStats) {
			Image checkbox_image = new Image(mainRes.checkmark());
			checkbox_image.setTitle("Click to add/remove "+ stat.getLabel()+" statistics to the chart as new series");
			checkbox_image.setAltText(stat.getName());
			Anchor link = new Anchor(stat.getLabel());
			link.setTitle("Click to display "+ stat.getLabel()+" statistics");
			link.setName(stat.getName());
			FlowPanel panel = new FlowPanel();
			SimplePanel panel_names  = new SimplePanel();
			panel.add(checkbox_image);
			panel.add(panel_names);
			panel_names.add(link);
			//panel.add(child)
			//SimplePanel panel = new SimplePanel(link);
			checkbox_image.addStyleName(mainRes.style().settingsContentItemCheckbox());
			panel_names.addStyleName(mainRes.style().settingsContentItemText());
			panel.addStyleName(mainRes.style().settingsContentItem());
			track_container.add(panel);
			handlerRegistrations.add(link.addClickHandler(text_click_handler));
			handlerRegistrations.add(checkbox_image.addClickHandler(image_click_handler));
		}
	}
	
	private void updateSettingChecked() {
		for (int i =0;i<track_container.getWidgetCount();i++) {
			FlowPanel panel = (FlowPanel)track_container.getWidget(i);
			Image image = (Image)panel.getWidget(0);
			for (GenomeStat stat: currentGenomeStats)  {
				if (stat.getName().equals(image.getAltText())) {
					image.addStyleName(mainRes.style().settingsContentItemCheckboxChecked());
					break;
				}
				else
					image.removeStyleName(mainRes.style().settingsContentItemCheckboxChecked());
			}
		}
	}

	private List<GenomeStat> getMissingGenomeStats() {
		if (!currentGenomeStats.get(0).isStackable()) {
			if (!nonstackableGenomeStatsCache.containsKey(currentGenomeStats.get(0)))
				return currentGenomeStats;
		}
		else
		{
			if (stackableGenomeStatsCache == null)
				return currentGenomeStats;
			List<GenomeStat> uncached_stats = new ArrayList<GenomeStat>();
			for (GenomeStat stat: currentGenomeStats) {
				if (stackableGenomeStatsCache.getColumnIndex(stat.getName()) == -1)
					uncached_stats.add(stat);
			}
			if (uncached_stats.size() == 0)
				return null;
			return uncached_stats;
		}
		return null;
	}
	
	protected void drawStatistics() {
		DataView view = filterStatsToDisplay();
		if (view != null) {
			genomeStatChart.draw(view,createOptions(currentGenomeStats.get(0).isStepPlot()));
			isGenomeStatsDrawn = true;
		}
	}
	
	protected DataView filterStatsToDisplay() {
		DataView view = null;
		if (currentGenomeStats == null || currentGenomeStats.size() == 0)
			return null;
		if (!currentGenomeStats.get(0).isStackable()) {
			if (!nonstackableGenomeStatsCache.containsKey(currentGenomeStats.get(0)))
				return null;
			view = DataView.create(nonstackableGenomeStatsCache.get(currentGenomeStats.get(0)));
		}
		else
		{
			if (stackableGenomeStatsCache == null)
				return null;
			view = DataView.create(stackableGenomeStatsCache);
			ArrayList<Integer> columns = new ArrayList<Integer>();
			columns.add(0);
			for (int i = 0;i<currentGenomeStats.size();i++) {
				GenomeStat stat  = currentGenomeStats.get(i);
				int index =  stackableGenomeStatsCache.getColumnIndex(stat.getName());
				if (index != -1)
					columns.add(index);
			}
			int[] columns_to_show  = new int[columns.size()];
			for (int i = 0;i<columns.size();i++) 
			columns_to_show[i] = columns.get(i).intValue();
			view.setColumns(columns_to_show);
		}
		return view;
	}
	
	private Dygraphs.Options createOptions(boolean stepPlot) {
		
		options.setRollerPeriod(1000);
		options.setshowRoller(true);
		options.setPointSize(0);
		options.setIncludeZero(true);
		options.setWidth(width);
		options.setHeight(statsBandHeight);
		options.setAxisLabelFontSize(11);
		options.setyAxisLabelWidth(20);
		options.setMinimumDistanceForHighlight(10);
		options.setFillGraph(true);
		if (stepPlot) {
			options.setStepPlot(stepPlot);
		}
		options.setLegend(SHOW_LEGEND.always);
		return options;
	}
	
	private void initStatisticsChart() {
		genomeStatChart.setWidth("100%");
		genomeStatChart.setHeight(statsBandHeight+"px");
		genomeStatChart.addUnderlayHandler(new UnderlayHandler() {
			
			@Override
			public void onUnderlay(UnderlayEvent event) {
				int zoomStart = processing.getInstance().getZoomStart();
				int zoomEnd = processing.getInstance().getZoomEnd();
				event.canvas.save();
				event.canvas.setFillStyle("yellow");
				double left = event.dygraph.toDomXCoord(zoomStart);
				double right = event.dygraph.toDomXCoord(zoomEnd);
				double length = right - left;
				if (length < 1)
				{
					left = left -0.5;
					length = 1;
				}
				event.canvas.fillRect(left, event.area.getY(), length, event.area.getH());
				event.canvas.restore();
			}
				
		}, options);
	}

	private void cacheDataTable(DataTable dataTable) {
		if (!currentGenomeStats.get(0).isStackable()) {
			nonstackableGenomeStatsCache.put(currentGenomeStats.get(0),dataTable);
		}
		else
		{
			if (stackableGenomeStatsCache == null)
				stackableGenomeStatsCache = dataTable;
			else {
				int rows = dataTable.getNumberOfRows();
				int cols = dataTable.getNumberOfColumns();
				int col_start = stackableGenomeStatsCache.getNumberOfColumns();
				for (int i = 1;i<cols;i++) {
					stackableGenomeStatsCache.addColumn(dataTable.getColumnType(i), dataTable.getColumnLabel(i),dataTable.getColumnId(i));
				}
				
				for (int i = 0;i<rows;i++) {
					int col_to_update = col_start;
					for (int j= 1;j<cols;j++) {
						if (dataTable.isValueNull(i, j))
							stackableGenomeStatsCache.setValueNull(i, col_to_update);
						else
							stackableGenomeStatsCache.setValue(i, col_to_update, dataTable.getValueDouble(i, j));
						col_to_update = col_to_update +1;
					}
				}
			}
		}
	}
	
	private DataTable combineStats(List<DataTable> dataTables) {
		DataTable data  = DataTable.create();
		data.addColumn(ColumnType.NUMBER, "Position", "position");
		int rows = 0;
		for (DataTable dataTable: dataTables) {
			data.addColumn(ColumnType.NUMBER,dataTable.getColumnLabel(1));
			rows = data.getNumberOfRows();
		}
		data.addRows(rows);
		for (int i =1;i<=dataTables.size() ;i++) {
			DataTable dataTable = dataTables.get(i);
			for (int j = 0;j<rows;j++) {
				data.setValue(j, i, dataTable.getValueDouble(j, 1));
			}
		}
		return data;
	}
	

	@Override
	protected void onUnload() {
		super.onUnload();
		description_popup.hide();
		settings_popup.hide();
		for (HandlerRegistration handlerRegistration: handlerRegistrations) {
			handlerRegistration.removeHandler();
		}
		handlerRegistrations.clear();
	}



	@Override
	public void removeFromParent() {
		settings_popup.hide();
		description_popup.hide();
		super.removeFromParent();
	}



	@Override
	public void setVisible(boolean visible) {
		if (!visible) {
			settings_popup.hide();
			description_popup.hide();
		}
		super.setVisible(visible);
	}
	
	
	@Override
	public void onResize() {
		scheduledLayout();
	}
	
	@Override 
	public void onAttach() {
		super.onAttach();
		scheduledLayout();
	}
	
	public void forceLayout() {
		if (!isAttached() || !isVisible())
			return;
		width = getElement().getClientWidth() - width_offset;
		height = getElement().getClientHeight() - (showStatsBand ? statsBandHeight : 0) - settings_btn.getElement().getClientHeight();
		if (processing.isLoaded()) {
			processing.getInstance().setLayoutSize(width, height);
			processing.getInstance().redraw(false);
			genomeStatChart.onResize();
		}
	}
	
	private void scheduledLayout() {
	    if (isAttached() && !layoutScheduled) {
	      layoutScheduled = true;
	      Scheduler.get().scheduleDeferred(layoutCmd);
	    }
	}
}
