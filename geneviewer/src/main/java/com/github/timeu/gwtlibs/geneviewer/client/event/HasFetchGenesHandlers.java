package com.github.timeu.gwtlibs.geneviewer.client.event;

import com.github.timeu.gwtlibs.geneviewer.client.event.FetchGeneEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasFetchGenesHandlers extends HasHandlers{
	
	HandlerRegistration addFetchGeneHandler(FetchGeneEvent.Handler handler);

}
