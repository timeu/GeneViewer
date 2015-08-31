package com.github.timeu.gwtlibs.geneviewer.client.event;


import com.google.gwt.core.client.js.JsProperty;
import com.google.gwt.core.client.js.JsType;

@JsType
public interface GeneViewerMouseMoveData {

	@JsProperty int getPosition();
	@JsProperty String getGene();
}
