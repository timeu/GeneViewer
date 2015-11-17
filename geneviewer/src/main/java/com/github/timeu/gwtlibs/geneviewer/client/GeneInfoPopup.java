package com.github.timeu.gwtlibs.geneviewer.client;

import com.github.timeu.gwtlibs.geneviewer.client.event.Gene;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by uemit.seren on 9/21/15.
 */
public class GeneInfoPopup extends PopupPanel {

    private static Binder uiBinder = GWT.create(Binder.class);

    interface Binder extends UiBinder<Widget, GeneInfoPopup> {	}

    @UiField
    SpanElement nameLb;
    @UiField
    Label infoLb;
    @UiField
    Label positionLb;
    public GeneInfoPopup() {
        setWidget(uiBinder.createAndBindUi(this));
    }

    public void setGene(Gene gene) {
        infoLb.setText(gene.description);
        nameLb.setInnerText(gene.name);
        positionLb.setText(gene.start + " - " + gene.end + " (" + gene.chromosome + ")");
    }

    public void setGeneInfo(String info) {
        infoLb.setText(info);
    }


}
