package at.gmi.nordborglab.widgets.geneviewer.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface FetchGeneHandler extends EventHandler{

	void onFetchGenes(FetchGeneEvent event);
}
