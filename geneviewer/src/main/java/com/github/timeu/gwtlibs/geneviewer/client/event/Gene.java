package com.github.timeu.gwtlibs.geneviewer.client.event;

import com.google.gwt.core.client.js.JsProperty;
import com.google.gwt.core.client.js.JsType;

@JsType
public interface Gene {
	
	@JsProperty  String getName();
    @JsProperty String getChromosome();
	@JsProperty int getStart();
    @JsProperty int getEnd();
    @JsProperty String getDescription();
}
