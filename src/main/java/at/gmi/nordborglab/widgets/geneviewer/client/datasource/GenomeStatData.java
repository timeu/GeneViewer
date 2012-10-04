package at.gmi.nordborglab.widgets.geneviewer.client.datasource;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.visualization.client.DataTable;

public class GenomeStatData extends BackendResult {

	protected GenomeStatData() {}
	
	private final native String getData() /*-{
		return this.data;
	}-*/;
	
	public final DataTable getDataTable() {
		return DataTable.create(JsonUtils.safeEval(getData()).cast());
	}
}
