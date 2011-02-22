package at.gmi.nordborglab.widgets.geneviewer.client.event;

import at.gmi.nordborglab.widgets.geneviewer.client.datasource.Gene;

import com.google.gwt.event.logical.shared.HasHighlightHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasHighlightGeneHandlers extends HasHandlers{
	HandlerRegistration addHighlightGeneHandler(HighlightGeneHandler handler);
}
