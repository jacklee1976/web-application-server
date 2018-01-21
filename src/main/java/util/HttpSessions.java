package util;

import java.util.HashMap;
import java.util.Map;

import util.HttpSession;

/**
 * @author zack <zack@nhn.com>
 * @since 2018. 01. 18.
 */
public class HttpSessions {
	private static Map<String, HttpSession> httpSessionMap = new HashMap<String, HttpSession>();

	public static HttpSession getSession(String id) {
		HttpSession session = httpSessionMap.get(id);

		if (session == null) {
			session = new HttpSession(id);
			httpSessionMap.put(id, session);

			return session;
		}

		return session;
	}

	public void remove(String id) {
		httpSessionMap.remove(id);
	}

}
