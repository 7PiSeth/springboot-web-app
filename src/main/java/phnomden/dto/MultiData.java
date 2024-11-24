package phnomden.dto;

import com.google.gson.internal.LinkedTreeMap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MultiData extends ArrayList<LinkedHashMap<String, Object>> implements Serializable {

	private static final long serialVersionUID = 8154940219462381299L;

	public MultiData() {
		super();
	}

	public MultiData(List map) {
		super(map);
	}

	public Object[] getKeys() {
		Object[] keyArr = new Object[0];
		if (size() > 0) {
			keyArr = get(0).keySet().toArray();
		}
		return keyArr;
	}

	public void put(Object key, List l) {
		for (int i = 0; i < l.size(); i++) {
			if (size() < i + 1) {
				add(new Data());
			}
			((Map) get(i)).put(key, l.get(i));
		}
	}

	public void putAll(List m) {
		clear();
		addAll(m);
	}

	public List<Data> get(Object key) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < size(); i++) {
			list.add(get(i).get(key));
		}
		return list;
	}

	public List<Data> toListData() {
		List<Data> listData = new ArrayList<Data>();
		for (LinkedHashMap<String, Object> map : this) {
			listData.add(new Data(map));
		}
		return listData;
	}

	public void addData(Data Data) {
		add(new Data(Data));
	}

	public void add(String key, Object value) {
		boolean add = false;
		for (int i = 0; i < size(); i++) {
			if (!get(i).containsKey(key)) {
				get(i).put(key, value);
				add = true;
				break;
			}
		}
		if (!add) {
			Data row = new Data();
			add(row);
		}
	}

	@SuppressWarnings({ "unchecked" })
	public Data getData(int key) {
		Object obj = get(key);
		if (obj instanceof Data) {
			return (Data) obj;
		} else if (obj instanceof LinkedHashMap) {
			return new Data((LinkedHashMap<String, Object>) obj);
		} else if (obj instanceof LinkedTreeMap) {
			LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>((LinkedTreeMap<String, Object>) obj);
			return new Data(linkedHashMap);
		}
		return obj == null ? new Data() : (obj instanceof Data ? (Data) obj : new Data());
	}

	public void addMultiData(MultiData Data) {
		int cnt = Data.size();
		for (int i = 0; i < cnt; i++) {
			this.add(new Data(Data.get(i)));
		}
	}

	public int getKeyCount() {
		int keyCount = 0;
		if (size() > 0)
			keyCount = get(0).keySet().size();
		return keyCount;
	}
}