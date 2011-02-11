package at.gmi.nordborglab.widgets.geneviewer.client.datasource;


public abstract class AbstractHttpDataSource implements DataSource{

	protected String url;
	
	public AbstractHttpDataSource(String url) {
		this.url = url;
	}
}
