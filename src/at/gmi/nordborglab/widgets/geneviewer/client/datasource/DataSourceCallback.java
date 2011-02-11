package at.gmi.nordborglab.widgets.geneviewer.client.datasource;

import com.google.gwt.core.client.JavaScriptObject;

public interface DataSourceCallback {
	public void onFetchGenes(JavaScriptObject genes);
}
