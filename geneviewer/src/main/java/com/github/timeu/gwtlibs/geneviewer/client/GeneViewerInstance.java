package com.github.timeu.gwtlibs.geneviewer.client;


import com.github.timeu.gwtlibs.geneviewer.client.event.EventCallback;
import com.github.timeu.gwtlibs.processingjsgwt.client.ProcessingInstance;

import com.google.gwt.core.client.JsArrayMixed;
import jsinterop.annotations.JsType;


@JsType(isNative = true)
public interface GeneViewerInstance extends ProcessingInstance {

	void api_setLength(int length);
	
	void api_updateZoom(int start,int end);
	
	int api_getZoomStart();

	int api_getZoomEnd();
	
	void api_setMaximumZoomToDraw(int maximumZoomToDraw);
	
	int api_getMaximumZoomToDraw();
	
	void api_setMaximumZoomToDrawFeatures(int maximumZoomToDrawFeatures);
	
	int api_getMaximumZoomToDrawFeatures();
	
	void api_setSelectionLine(int position);
	
	void api_setSelectionLinePx(int position_px);
	
	void api_addEventHandler(String callback, EventCallback handler);

	void api_clearEventHandlers();

	void api_hideSelectionLine();

	void api_setGeneData(JsArrayMixed geneData);

	void api_setChromosome(String chromosome);

	void api_setViewRegion(int viewStart, int viewEnd);
	
	int api_getViewStart();
	
	int api_getViewEnd();
	
	void api_redraw(boolean isFetchGenes);

	void api_setSize(int width, int height);

	void api_setShowRangeSelector(int show_range_selector);
}
