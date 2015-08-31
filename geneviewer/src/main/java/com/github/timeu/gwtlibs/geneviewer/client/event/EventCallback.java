package com.github.timeu.gwtlibs.geneviewer.client.event;

import com.google.gwt.core.client.js.JsFunction;

/**
 * Created by uemit.seren on 8/28/15.
 */
@JsFunction
@FunctionalInterface
public interface EventCallback<T> {
    void onCall(T data);
}
