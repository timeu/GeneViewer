package at.gmi.nordborglab.widgets.geneviewer.client.datasource;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.visualization.client.DataTable;

public class LocalStorageImpl implements Cache{
	
	public static enum TYPE {LOCAL,SESSION} 
	private TYPE type;
	private Storage cacheStorage = null;
	
	public LocalStorageImpl(TYPE type) throws Exception {
		this.type = type;
		if (type == TYPE.LOCAL) {
			cacheStorage = Storage.getLocalStorageIfSupported();
		}
		else {
			cacheStorage = Storage.getSessionStorageIfSupported();
		}
		if (cacheStorage == null) {
			throw new Exception("LocalStorage not supported");
		}
	}
	

	@Override
	public void put(Object key, Object value) {
		if (key == null) {
			throw new NullPointerException("key is null");
		}
		if (value == null) {
			throw new NullPointerException("value is null");
		}
		String jsonData = null;
		if (value instanceof List) {
			JSONArray array = new JSONArray();
			int index = 0;
			for (Object val:(List)value) {
				array.set(index,new JSONObject((JavaScriptObject)val));
				index = index +1;
			}
			jsonData = array.toString();
		}
		else
		{
			jsonData = ((CustomDataTable)value).toJSON();
		}
		cacheStorage.setItem(key.toString(), jsonData);
	}

	@Override
	public Object get(Object key) {
		if (key == null) {
			throw new NullPointerException("key is null");
		}
		String jsonDataString = cacheStorage.getItem(key.toString());
		if (jsonDataString == null) {
			return null;
		}
		Object data = null;
		JavaScriptObject jsonData = JsonUtils.safeEval(jsonDataString);
		if (!key.equals("genomestatslist")) 
			data = DataTable.create((JavaScriptObject)jsonData);
		else if (jsonData instanceof JsArray){
			JsArray<GenomeStat> jsonStats = (JsArray<GenomeStat>)jsonData;
			List<GenomeStat> stats = new ArrayList<GenomeStat>();
			for (int i = 0;i<jsonStats.length();i++) {
				stats.add(jsonStats.get(i));
			}
			data = (Object)stats;
		}
		return data;
	}

	@Override
	public void remove(Object key) {
		cacheStorage.removeItem(key.toString());
	}

	@Override
	public void clear() {
		cacheStorage.clear();
		
	}
	
	public TYPE getType() {
		return type;
	}

}
