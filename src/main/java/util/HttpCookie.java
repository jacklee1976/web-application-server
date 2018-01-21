package util;

import java.util.Map;

/**
 * @author zack <zack@nhn.com>
 * @since 2018. 01. 18.
 */
public class HttpCookie {
	private Map<String, String> cookie;

	public HttpCookie(String cookieValue){
		cookie = HttpRequestUtils.parseCookies(cookieValue);
	}

	public String getCookie(String name){
		return cookie.get(name);
	}
}
