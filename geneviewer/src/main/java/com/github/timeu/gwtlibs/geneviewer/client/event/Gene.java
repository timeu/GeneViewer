package com.github.timeu.gwtlibs.geneviewer.client.event;


import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true,namespace = JsPackage.GLOBAL,name="Object")
public class Gene {

    public String name;

    public String chromosome;

    public int start;

    public int end;

    public String description;

    @JsOverlay
    public final static Gene create(String name,String chromosome,int start,int end,String description) {
        Gene gene = new Gene();
        gene.name = name;
        gene.chromosome = chromosome;
        gene.start = start;
        gene.end = end;
        gene.description = description;
        return gene;
    }
}
