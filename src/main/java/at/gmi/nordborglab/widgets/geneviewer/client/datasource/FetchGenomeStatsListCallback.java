package at.gmi.nordborglab.widgets.geneviewer.client.datasource;

import java.util.List;

public interface FetchGenomeStatsListCallback {
	void onFetchGenomeStatsList(List<GenomeStat> genomeStats);
}
