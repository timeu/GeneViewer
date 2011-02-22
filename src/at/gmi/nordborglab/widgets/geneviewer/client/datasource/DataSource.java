package at.gmi.nordborglab.widgets.geneviewer.client.datasource;


public interface DataSource {
	
	public void fetchGenes(String chr, int start, int end, boolean getFeatures, DataSourceCallback callback);
	public void fetchGenesFromPrefix(String query,FetchGenesFromQueryCallback callback);
	public void searchGene(String query, SearchGeneCallback callback);
	public void fetchGeneDescription(String name,FetchGeneDescriptionCallback callback);
}
