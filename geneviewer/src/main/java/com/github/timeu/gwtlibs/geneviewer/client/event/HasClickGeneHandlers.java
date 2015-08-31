package com.github.timeu.gwtlibs.geneviewer.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasClickGeneHandlers extends HasHandlers{

	HandlerRegistration addClickGeneHandler(ClickGeneEvent.Handler handler);
}
