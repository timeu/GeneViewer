package com.github.timeu.gwtlibs.geneviewer.client.event;

import com.google.gwt.core.client.js.JsProperty;
import com.google.gwt.core.client.js.JsType;

/**
 * Created by uemit.seren on 8/31/15.
 */
@JsType
public interface Region {

    @JsProperty int getStart();
    @JsProperty int getEnd();
}
