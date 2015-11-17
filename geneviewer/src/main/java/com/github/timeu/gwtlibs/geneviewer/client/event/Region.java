package com.github.timeu.gwtlibs.geneviewer.client.event;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Created by uemit.seren on 8/31/15.
 */
@JsType(isNative = true,namespace = JsPackage.GLOBAL,name="Object")
public class Region {
    public int start;
    public int end;
}
