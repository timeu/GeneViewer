package at.gmi.nordborglab.widgets.geneviewer.client.datasource.impl;

import at.gmi.nordborglab.widgets.geneviewer.client.datasource.AbstractHttpDataSource;
import at.gmi.nordborglab.widgets.geneviewer.client.datasource.DataSourceCallback;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class JBrowseDataSourceImpl extends AbstractHttpDataSource{

		
	public JBrowseDataSourceImpl(String url) {
		super(url);
	}

	@Override
	public void fetchGenes(String chr,int start, int end, boolean getFeatures, final DataSourceCallback callback) {
		RequestBuilder request = new RequestBuilder(RequestBuilder.GET,url+"?chromosome="+chr+"&start=" + start+ "&end="+ end+"&isFeatures=" + getFeatures);
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
}
