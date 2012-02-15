package at.gmi.nordborglab.widgets.geneviewer.client.datasource;

import java.util.HashMap;

public class DefaultCacheImpl implements Cache {

	private HashMap<Object, Object> map;

	public DefaultCacheImpl() {
		this.map = new HashMap<Object, Object>();
	}

	@Override
	public void put(Object key, Object value) {
		if (key == null) {
			throw new NullPointerException("key is null");
		}
		if (value == null) {
			throw new NullPointerException("value is null");
		}
		map.put(key, value);
	}

	@Override
	public Object get(Object key) {
		// Check for null as Cache should not store null values / keys
		if (key == null) {
			throw new NullPointerException("key is null");
		}
		return map.get(key);
	}

	@Override
	public void remove(Object key) {
		map.remove(key);
	}

	@Override
	public void clear() {
		map.clear();
	}

}
