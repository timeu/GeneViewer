package at.gmi.nordborglab.widgets.geneviewer.client.datasource.impl;

import java.util.List;

import com.google.gwt.visualization.client.DataTable;

import at.gmi.nordborglab.widgets.geneviewer.client.datasource.Cache;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.FetchGenomeStatsDataCallback;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.FetchGenomeStatsListCallback;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.GenomeStat;


public class JBrowseCacheDataSourceImpl extends JBrowseDataSourceImpl {

	protected final Cache cache;
	private static String genomeStatsListKey  ="genomestatslist";
	
	public JBrowseCacheDataSourceImpl(String url,final Cache cache) {
		super(url);
		this.cache = cache;
	}

	@Override
	public void fetchGenomeStatsData(List<GenomeStat> genomeStats, String chr,
			final FetchGenomeStatsDataCallback callback) {
		final String genomeStatsDataKey = getStatUrl(genomeStats)+"_"+chr;
		Object value = cache.get(genomeStatsDataKey);
		if (value != null && value instanceof DataTable) {
			callback.onFetchGenomeStats((DataTable)value);
		}
		else {
			super.fetchGenomeStatsData(genomeStats, chr,new FetchGenomeStatsDataCallback() {
				
				@Override
				public void onFetchGenomeStats(DataTable statsDataTable) {
					cache.put(genomeStatsDataKey, statsDataTable);
					callback.onFetchGenomeStats(statsDataTable);
				}
			});
		}
	}

	@Override
	public void fetchGenomeStatsList(final FetchGenomeStatsListCallback callback) {
		Object value = cache.get(genomeStatsListKey);
		if (value != null && value instanceof List) {
			callback.onFetchGenomeStatsList((List<GenomeStat>)value);
		}
		else {
			super.fetchGenomeStatsList(new FetchGenomeStatsListCallback() {
				
				@Override
				public void onFetchGenomeStatsList(List<GenomeStat> genomeStats) {
					cache.put(genomeStatsListKey, genomeStats);
					callback.onFetchGenomeStatsList(genomeStats);
				}
			});
		}
	}
	

}
