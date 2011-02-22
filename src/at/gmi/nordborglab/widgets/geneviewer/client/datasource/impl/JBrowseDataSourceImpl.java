package at.gmi.nordborglab.widgets.geneviewer.client.datasource.impl;

import java.util.ArrayList;

import at.gmi.nordborglab.widgets.geneviewer.client.datasource.AbstractHttpDataSource;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.DataSourceCallback;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.FetchGeneDescriptionCallback;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.FetchGenesFromQueryCallback;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.Gene;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.SearchGeneCallback;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class JBrowseDataSourceImpl extends AbstractHttpDataSource{

		
	public JBrowseDataSourceImpl(String url) {
		super(url);
	}

	@Override
	public void fetchGenes(String chr,int start, int end, boolean getFeatures, final DataSourceCallback callback) {
		RequestBuilder request = new RequestBuilder(RequestBuilder.GET,url+"getGenes?chromosome="+chr+"&start=" + start+ "&end="+ end+"&isFeatures=" + getFeatures);
		request.setCallback(new RequestCallback() {
			
			@Override
			public void onResponseReceived(Request request, Response response) {
				JSONValue value = JSONParser.parseStrict(response.getText());
				JSONObject retval = value.isObject();
				if (retval.get("status").isString().stringValue().equals("OK"))
				{
					JSONArray genes_json = retval.get("genes").isArray();
					callback.onFetchGenes(genes_json.getJavaScriptObject());
				}
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				// TODO Auto-generated method stub
			}
		});
		try
		{
			request.send();
		}
		catch (Exception e) {
			
		}
	}

	@Override
	public void searchGene(String query, final SearchGeneCallback callback) {
		RequestBuilder request = new RequestBuilder(RequestBuilder.GET,url+"getGeneFromName?query="+query);
		request.setCallback(new RequestCallback() {
			
			@Override
			public void onResponseReceived(Request request, Response response) {
				JSONValue value = JSONParser.parseStrict(response.getText());
				JSONObject retval = value.isObject();
				if (retval.get("status").isString().stringValue().equals("OK"))
				{
					JSONArray gene_json = retval.get("gene").isArray();
					Gene gene = new Gene(gene_json.get(1).isString().stringValue(),
							gene_json.get(2).isString().stringValue(),(int)gene_json.get(3).isNumber().doubleValue(),(int)gene_json.get(4).isNumber().doubleValue(),gene_json.get(5).isString().stringValue());
					callback.onSearchGenes(gene);
				}
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				// TODO Auto-generated method stub
			}
		});
		try
		{
			request.send();
		}
		catch (Exception e) {
			
		}
		
	}

	@Override
	public void fetchGenesFromPrefix(String query,final FetchGenesFromQueryCallback callback) {
		RequestBuilder request = new RequestBuilder(RequestBuilder.GET,url+"getGenesFromQuery?query="+query);
		request.setCallback(new RequestCallback() {
			
			@Override
			public void onResponseReceived(Request request, Response response) {
				JSONValue value = JSONParser.parseStrict(response.getText());
				JSONObject retval = value.isObject();
				if (retval.get("status").isString().stringValue().equals("OK"))
				{
					ArrayList<Gene> gene_list = new ArrayList<Gene>();
					JSONArray genes_json = retval.get("genes").isArray();
					JSONNumber count_json = retval.get("count").isNumber();
					Boolean isMore = retval.get("isMore").isBoolean().booleanValue();
					Double count = null;
					if (count_json != null)
						count = count_json.doubleValue();
					for (int i = 0;i<genes_json.size();i++)
					{
						JSONArray gene_json = genes_json.get(i).isArray();
						Gene gene = new Gene(gene_json.get(1).isString().stringValue(),
								gene_json.get(2).isString().stringValue(),(int)gene_json.get(3).isNumber().doubleValue(),(int)gene_json.get(4).isNumber().doubleValue(),gene_json.get(5).isString().stringValue());
						gene_list.add(gene);
					}
					callback.onFetchGenesFromQuery(gene_list,count.intValue(),isMore);
				}
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				// TODO Auto-generated method stub
			}
		});
		try
		{
			request.send();
		}
		catch (Exception e) {
			
		}
	}

	@Override
	public void fetchGeneDescription(String name, final
			FetchGeneDescriptionCallback callback) {
		RequestBuilder request = new RequestBuilder(RequestBuilder.GET,url+"getGeneDescription?gene="+name);
		request.setCallback(new RequestCallback() {
			
			@Override
			public void onResponseReceived(Request request, Response response) {
				JSONValue value = JSONParser.parseStrict(response.getText());
				JSONObject retval = value.isObject();
				if (retval.get("status").isString().stringValue().equals("OK"))
				{
					String description = retval.get("description").isString().stringValue();
					callback.onFetchGeneDescription(description);
				}
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				// TODO Auto-generated method stub
			}
		});
		try
		{
			request.send();
		}
		catch (Exception e) {
			
		}
	}
}
