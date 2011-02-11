package at.gmi.nordborglab.widgets.geneviewer.client.datasource;


public interface DataSource {
	
	public void fetchGenes(String chr, int start, int end, boolean getFeatures, DataSourceCallback callback);
}
