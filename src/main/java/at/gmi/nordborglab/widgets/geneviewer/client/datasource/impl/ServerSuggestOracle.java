package at.gmi.nordborglab.widgets.geneviewer.client.datasource.impl;

import java.util.ArrayList;

import at.gmi.nordborglab.widgets.geneviewer.client.datasource.DataSource;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.FetchGenesFromQueryCallback;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.Gene;

import com.google.gwt.user.client.ui.SuggestOracle;

public class ServerSuggestOracle extends SuggestOracle{

	protected DataSource datasource;
	protected int startQueryLength;
	protected ArrayList<GeneSuggestion> suggestions = new ArrayList<GeneSuggestion>();
	protected boolean isMoreSuggestions = false;
	protected int previousQueryLength = 0;
	
	public ServerSuggestOracle(DataSource datasource,int startQueryLength) 
	{
		super();
		this.datasource = datasource;
		this.startQueryLength = startQueryLength;
		
	}
	
	@Override
	public void requestSuggestions(final Request request, final Callback callback) {
		if (request.getQuery().length() < startQueryLength) 
			return;
		if (isMoreSuggestions || previousQueryLength > request.getQuery().length() || suggestions.size() == 0) 
		{
			datasource.fetchGenesFromPrefix(request.getQuery(), new FetchGenesFromQueryCallback() {
				
				@Override
				public void onFetchGenesFromQuery(ArrayList<Gene> genes,Integer count,boolean isMore) {
					
					suggestions.clear();
					for (int i = 0;i<genes.size();i++) {
						GeneSuggestion suggestion = new GeneSuggestion(genes.get(i));
						suggestions.add(suggestion);
					}
					SuggestOracle.Response response = new SuggestOracle.Response(suggestions);
					isMoreSuggestions = isMore; 
					if (count != null)
						response.setMoreSuggestionsCount(count);
					else
						response.setMoreSuggestions(isMore);
					previousQueryLength =  request.getQuery().length();
					callback.onSuggestionsReady(request,response);
				}
			});
		}
		else
		{
			ArrayList<GeneSuggestion> new_suggestion_list = new ArrayList<GeneSuggestion>(); 
			for (GeneSuggestion suggestion:suggestions) {
				if (suggestion.getGene().getName().startsWith(request.getQuery()))
					new_suggestion_list.add(suggestion);
			}
			SuggestOracle.Response response = new SuggestOracle.Response(new_suggestion_list);
			callback.onSuggestionsReady(request,response);
		}
	}

}
