package at.gmi.nordborglab.widgets.geneviewer.client.datasource;

import java.util.ArrayList;

public interface FetchGenesFromQueryCallback {
	void onFetchGenesFromQuery(ArrayList<Gene> genes,Integer count, boolean isMore);
}
