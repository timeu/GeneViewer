package at.gmi.nordborglab.widgets.geneviewer.client.datasource;

import com.google.gwt.core.client.JavaScriptObject;

public class BackendResult extends JavaScriptObject {

	protected BackendResult() {
	}

	public final native String getStatus() /*-{
		return this.status;
	}-*/;

	public final native String getStatusText() /*-{
		return this.statustext;
	}-*/;
}
