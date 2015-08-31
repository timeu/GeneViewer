package com.github.timeu.gwtlibs.geneviewer.client.event;

import com.google.gwt.core.client.js.JsProperty;
import com.google.gwt.core.client.js.JsType;

/**
 * Created by uemit.seren on 8/28/15.
 */
@JsType
public interface GeneHighlightData {

    @JsProperty Gene getGene();
    @JsProperty int getX();
    @JsProperty int getY();
}
