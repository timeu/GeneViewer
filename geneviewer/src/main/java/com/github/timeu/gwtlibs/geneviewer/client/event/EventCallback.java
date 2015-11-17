package com.github.timeu.gwtlibs.geneviewer.client.event;


import jsinterop.annotations.JsFunction;

/**
 * Created by uemit.seren on 8/28/15.
 */
@JsFunction
@FunctionalInterface
public interface EventCallback<T> {
    void onCall(T data);
}
