package at.gmi.nordborglab.widgets.geneviewer.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasClickGeneHandlers extends HasHandlers{

	HandlerRegistration addClickGeneHandler(ClickGeneHandler handler);
}
