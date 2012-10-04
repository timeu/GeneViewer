package at.gmi.nordborglab.widgets.geneviewer.client.datasource;

import com.google.gwt.core.client.JsArray;

public class GenomeStatsList extends BackendResult{
	
	protected GenomeStatsList() {}
	
	

	public final native JsArray<GenomeStat> getGenomeStats() /*-{
		return this.stats;
	}-*/;

}
