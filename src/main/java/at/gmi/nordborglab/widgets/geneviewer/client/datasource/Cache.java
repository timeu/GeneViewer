package at.gmi.nordborglab.widgets.geneviewer.client.datasource;

public interface Cache {

	void put(Object key, Object value);
	
	Object get(Object key);
	
	void remove(Object key);
	 
	void clear();
	
}
