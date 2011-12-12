package at.gmi.nordborglab.widgets.geneviewer.client.event;


import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasUnhighlightGeneHandlers extends HasHandlers{

	HandlerRegistration addUnhighlightGeneHandlers(UnhighlightGeneHandler handler); 
}
