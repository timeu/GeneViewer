package at.gmi.nordborglab.widgets.geneviewer.client.datasource;

import com.google.gwt.visualization.client.DataTable;

public class CustomDataTable extends DataTable {
	
	protected CustomDataTable() {}
	
	public final native String toJSON() /*-{
		return this.toJSON();
	}-*/;
}
