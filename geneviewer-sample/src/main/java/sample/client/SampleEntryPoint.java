/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package sample.client;

import com.github.timeu.gwtlibs.geneviewer.client.GeneViewer;
import com.github.timeu.gwtlibs.geneviewer.client.event.ClickGeneEvent;
import com.github.timeu.gwtlibs.geneviewer.client.event.FetchGeneEvent;
import com.github.timeu.gwtlibs.geneviewer.client.event.Gene;
import com.github.timeu.gwtlibs.geneviewer.client.event.HighlightGeneEvent;
import com.github.timeu.gwtlibs.geneviewer.client.event.MouseMoveEvent;
import com.github.timeu.gwtlibs.geneviewer.client.event.UnhighlightGeneEvent;
import com.github.timeu.gwtlibs.geneviewer.client.event.ZoomResizeEvent;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;


/**
 * Initializes the application. Nothing to see here: everything interesting
 * happens in the presenters.
 */
public class SampleEntryPoint implements EntryPoint {

    public interface DataBundle extends ClientBundle {
        DataBundle INSTANCE = GWT.create(DataBundle.class);

        @Source("data/genes.json")
        TextResource genes();

        @Source("data/genes_with_features.json")
        TextResource genes_with_features();
    }

    final GeneViewer geneviewer = new GeneViewer();
    HTML eventPanel = new HTML();
    HTML zoomLabel = new HTML();
    int regionSize = 0;
    final int sparseZoomAmount = 1000000;
    final int denseZoomAmount = 10000;
    RadioButton genesRd = new RadioButton("type","Genes without features");
    RadioButton genesWithFeaturesRd = new RadioButton("type","Genes with features");
    int[] geneRegion = new int[]{9778876,10633663};
    int[] geneWithFeaturesRegion = new int[]{10355429,10432933};
    boolean isFeatures = false;

    @Override
    public void onModuleLoad() {
        geneviewer.setChromosome("Chr1");
        geneviewer.setViewRegion(geneRegion[0],geneRegion[1]);
        FlowPanel settingsPanel = new FlowPanel();
        LayoutPanel panel = new LayoutPanel();

        ScrollPanel eventsPanel = new ScrollPanel();

        Button highlightBtn = new Button("Highlight position 10976308");

        highlightBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                geneviewer.setSelectionLine(10183965);
            }
        });
        Button resetZoomBtn = new Button("Reset zoom");
        resetZoomBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                geneviewer.resetZoom();
            }
        });
        genesRd.setValue(true);
        genesRd.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                changeType(false);
            }
        });

        genesWithFeaturesRd.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                changeType(true);
            }
        });
        HTML description = new HTML("<h2>How to use:</h2>" +
                "<p><b>Zooming:</b>Left-Click + hold and drag</p>" +
                "<p><b>Panning:</b>Right-Click + hold and drag</p>" +
                "<p>Clicking on a gene will fire the ClickGeneEvent and moving the mouse over a gene will fire the HighlightGeneEvent</p>");
        settingsPanel.add(description);
        settingsPanel.add(genesRd);
        settingsPanel.add(genesWithFeaturesRd);
        settingsPanel.add(zoomLabel);
        settingsPanel.add(highlightBtn);
        settingsPanel.add(resetZoomBtn);

        panel.add(settingsPanel);
        panel.add(geneviewer);
        panel.add(eventsPanel);
        eventPanel.setHTML("<p><b>Events:</b></p>");
        eventsPanel.add(eventPanel);

        panel.setWidgetTopHeight(settingsPanel, 0D, Style.Unit.PX, 250, Style.Unit.PX);
        panel.setWidgetTopHeight(geneviewer,250, Style.Unit.PX,200, Style.Unit.PX);
        panel.setWidgetTopBottom(eventsPanel, 450, Style.Unit.PX, 0, Style.Unit.PX);
        RootLayoutPanel.get().add(panel);
        sinkEvents();
        try {
            geneviewer.load(new Runnable() {
                @Override
                public void run() {
                    changeType(false);
                }
            });
        } catch (Exception e) {
            GWT.log("Error loading GeneViewer", e);
        }
    }

    private void sinkEvents() {
        geneviewer.addClickGeneHandler(new ClickGeneEvent.Handler() {
            @Override
            public void onClickGene(ClickGeneEvent event) {
                logEvent("ClickGeneEvent called: Gene: " + getMessageFromGene(event.getGene()));
            }
        });

        geneviewer.addUnhighlightGeneHandlers(new UnhighlightGeneEvent.Handler() {

            @Override
            public void onUnhighlightGene(UnhighlightGeneEvent event) {
                logEvent("UnhighlightGeneEvent called");
            }
        });
        geneviewer.addHighlightGeneHandler(new HighlightGeneEvent.Handler() {
            @Override
            public void onHightlightGene(HighlightGeneEvent event) {
                logEvent("HighlightGeneEvent called: Gene: " + getMessageFromGene(event.getGene()));
            }
        });
        geneviewer.addMouseMoveHandler(new MouseMoveEvent.Handler() {
            @Override
            public void onMoveMove(MouseMoveEvent event) {
                //logEvent("MouseMoveEvent called: gene: " + event.getData().getGene()+", position:"+event.getData().getPosition());
            }
        });
        geneviewer.addFetchGeneHandler(new FetchGeneEvent.Handler() {
            @Override
            public void onFetchGenes(FetchGeneEvent event) {
                logEvent("FetchGeneEvent called: start: " + event.getStart() + ", end:" + event.getEnd());
                JsArrayMixed data = getData(isFeatures);
                geneviewer.setGeneData(data);
            }
        });

        geneviewer.addZoomResizeHandler(new ZoomResizeEvent.Handler() {
            @Override
            public void onZoomResize(ZoomResizeEvent event) {
                zoomLabel.setHTML("<b>"+event.start+"</b> - <b>" + event.stop+"</b>");
                logEvent("ZoomResizeEvent called: start: " + event.start + ", end:" + event.stop);
            }
        });
    }

    private String getMessageFromGene(Gene gene) {
        return "Name: "+gene.name+", start: "+gene.start+", end: " + gene.end+", chr: " + gene.chromosome;
    }

    private void changeType(boolean isFeatures)
    {   this.isFeatures = isFeatures;
        JsArrayMixed data = getData(isFeatures);
        int[] region = isFeatures ? geneWithFeaturesRegion : geneRegion;
        zoomLabel.setHTML("<b>"+region[0] + "</b> - <b>" + region[1]+"</b>");
        geneviewer.setViewRegion(region[0],region[1]);
        geneviewer.setGeneData(data);
    }

    private JsArrayMixed getData(boolean isFeatures) {
        final String jsonData = isFeatures ? DataBundle.INSTANCE.genes_with_features().getText() : DataBundle.INSTANCE.genes().getText();
        JsArrayMixed data = JsonUtils.safeEval(jsonData);
        return data;
    }

    private void logEvent(String event) {
        eventPanel.setHTML(eventPanel.getHTML()+"<div>"+event+"</div>");
    }
}
