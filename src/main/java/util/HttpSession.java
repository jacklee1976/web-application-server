package util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author zack <zack@nhn.com>
 * @since 2018. 01. 18.
 */
public class HttpSession {
	private String id;

	UUID uuid = UUID.randomUUID();

	private Map<String, Object> attribute = new HashMap<String, Object>();



	public HttpSession(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setAttributes(String name, Object value){
		attribute.put(name, value);
	}

	public Object getAttribute(String name){
		return attribute.get(name);
	}

	public void removeAttribute(String name){
		attribute.remove(name);
	}

	public void invalidate(){
		attribute.clear();
	}

}
