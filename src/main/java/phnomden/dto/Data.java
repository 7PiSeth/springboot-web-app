package phnomden.dto;

import com.google.gson.internal.LinkedTreeMap;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Data extends LinkedHashMap<String, Object> implements Serializable {

	private static final long serialVersionUID = -5861114305569703387L;

	public Data() {
		super();
	}

	public Data(Map<String, Object> map) {
		super(map);
	}
	
	public Object getObj(String key) {
		if (get(key) != null) {
			return (get(key));
		}
		return null;
	}
	
	public List<?> getList(String key) {
		if (get(key) != null) {
			return (List<?>)get(key);
		}
		return Collections.emptyList();
	}

	public String getString(String key) {
		if (get(key) != null) {
			return String.valueOf(get(key));
		}
		return null;
	}

	public BigDecimal getBigDecimal(String key) {
		if (get(key) != null && !getString(key).isEmpty()) {
			return new BigDecimal(getString(key));
		}
		return BigDecimal.ZERO;
	}

	public long getLong(String key) {
		if (get(key) != null) {
			return Long.valueOf(getString(key)).longValue();
		}
		return 0L;
	}

	public int getInt(String key) {
		if (get(key) != null) {
			return Integer.valueOf(getString(key)).intValue();
		}
		return 0;
	}

	public Boolean getBoolean(String key) {
		if (get(key) != null) {
			return Boolean.valueOf(getString(key)).booleanValue();
		}
		return null;
	}

	public short getShort(String key) {
		if (get(key) != null) {
			return Short.valueOf(getString(key)).shortValue();
		}
		return 0;
	}

	public double getDouble(String key) {
		if (get(key) != null) {
			return Double.valueOf(getString(key)).doubleValue();
		}
		return 0.0D;
	}

	public float getFloat(String key) {
		if (get(key) != null) {
			return Float.valueOf(getString(key)).floatValue();
		}
		return 0.0F;
	}

	@SuppressWarnings({ "unchecked" })
	public Data getData(String key) {
		try {
			Object obj = get(key);
			if (obj instanceof Data) {
				return (Data) obj;
			} else if (obj instanceof LinkedHashMap) {
				return new Data((LinkedHashMap<String, Object>) obj);
			} else if (obj instanceof LinkedTreeMap) {
				LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>((LinkedTreeMap<String, Object>) obj);
				return new Data(linkedHashMap);
			} else {
				return obj == null ? new Data() : (Data) obj;
			}
		} catch (Exception e) {
			log.error("<<<< Error: {}", e);
		}
		return new Data();
	}

	public void setObj(String key, Object value) {
		this.put(key, value);
	}

	public void setString(String key, String value) {
		this.put(key, value);
	}

	public void setBigDecimal(String key, BigDecimal value) {
		this.put(key, value);
	}

	public void setLong(String key, long value) {
		this.put(key, value);
	}

	public void setInt(String key, int value) {
		this.put(key, value);
	}

	public void setBoolean(String key, boolean value) {
		this.put(key, value);
	}

	public void setShort(String key, short value) {
		this.put(key, value);
	}

	public void setDouble(String key, double value) {
		this.put(key, value);
	}

	public void setFloat(String key, float value) {
		this.put(key, value);
	}

	public void setData(String key, Data value) {
		this.put(key, value);
	}

	public void setMultiData(String key, MultiData value) {
		this.put(key, value);
	}

	@SuppressWarnings({ "unchecked" })
	public MultiData getMultiData(String key) {
		try {
			Object obj = get(key);
			if (obj instanceof MultiData) {
				return (MultiData) obj;
			} else if (obj instanceof ArrayList) {
	            List<?> list = (List<?>) obj;
	            if (!list.isEmpty() && list.get(0) instanceof LinkedTreeMap) {
	                List<LinkedHashMap<String, Object>> convertedList = new ArrayList<>();
	                for (Object item : list) {
	                    if (item instanceof LinkedTreeMap) {
	                        convertedList.add(new LinkedHashMap<>((LinkedTreeMap<String, Object>) item));
	                    }
	                }
	                return new MultiData(convertedList);
	            }
				return new MultiData((List<LinkedHashMap<String, Object>>) obj);
			} else {
				return obj == null ? new MultiData() : (MultiData) obj;
			}
		} catch (Exception e) {
			log.error("<<<< Error: {}", e);
		}
		return new MultiData();
	}

	public void appendFrom(Data data) {
		this.putAll(data);
	}
}