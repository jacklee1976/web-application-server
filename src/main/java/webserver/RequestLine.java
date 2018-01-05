package webserver;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpMethod;
import util.HttpRequestUtils;

/**
 * @author zack <zack@nhn.com>
 * @since 2018. 01. 04.
 */
public class RequestLine {
	private static final Logger log = LoggerFactory.getLogger(RequestLine.class);
	private HttpMethod method;
	private String path;
	private Map<String, String> parameters = new HashMap<String, String>();

	public void processRequestLine(String requestLine) {
		log.debug("processRequestLine : {}", requestLine);
		String[] tokens = requestLine.split(" ");

		if (tokens.length != 3) {
			throw new IllegalArgumentException(requestLine + "이 형식이 맞지 않습니다.");
		}

		method = HttpMethod.valueOf(tokens[0]);

		if ("POST".equals(method)) {
			path = tokens[1];
			return;
		}

		int index = tokens[1].indexOf("?");
		if (index == -1) {
			path = tokens[1];
		} else {
			path = tokens[1].substring(0, index);
			String queryString = tokens[1].substring(index + 1);
			parameters = HttpRequestUtils.parseQueryString(queryString);
		}

	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public Map<String, String> getParameter() {
		return parameters;
	}

}
