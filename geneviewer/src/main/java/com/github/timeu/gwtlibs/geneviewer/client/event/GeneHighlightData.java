package com.github.timeu.gwtlibs.geneviewer.client.event;


import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Created by uemit.seren on 8/28/15.
 */
@JsType(isNative = true,namespace = JsPackage.GLOBAL,name="Object")
public class GeneHighlightData {
    public Gene gene;
    public int x;
    public int y;
}
