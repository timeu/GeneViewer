package com.github.timeu.gwtlibs.geneviewer.client.event;


import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true,namespace = JsPackage.GLOBAL,name="Object")
public class GeneViewerMouseMoveData {
	public int position;
	public String gene;

}
