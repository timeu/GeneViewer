package at.gmi.nordborglab.widgets.geneviewer.client.datasource;

import java.util.List;


public interface DataSource {
	
	public void fetchGenes(String chr, int start, int end, boolean getFeatures, DataSourceCallback callback);
	public void fetchGenesFromPrefix(String query,FetchGenesFromQueryCallback callback);
	public void searchGene(String query, SearchGeneCallback callback);
	public void fetchGeneDescription(String name,FetchGeneDescriptionCallback callback);
	public void fetchGenomeStatsData(List<GenomeStat> genomeStats,String chr,FetchGenomeStatsDataCallback callback);
	public void fetchGenomeStatsList(FetchGenomeStatsListCallback callback);
}