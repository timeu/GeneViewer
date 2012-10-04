package at.gmi.nordborglab.widgets.geneviewer.client.datasource;


public class UploadGenomeStatResult extends BackendResult {

	protected UploadGenomeStatResult() {
	}
	
	public final native GenomeStat getData() /*-{
		return this.data;
	}-*/;

}
