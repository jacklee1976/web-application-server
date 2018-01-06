package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
public class HttpRequest {
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

	private static final String URI_ENCODEING = "UTF-8";

	private RequestLine requestLine;
	private Map<String, String> headers = new HashMap<String, String>();
	private Map<String, String> parameters = new HashMap<String, String>();

	public HttpRequest(InputStream is) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(is, URI_ENCODEING));

		String line = bf.readLine();
		if (line == null) {
			return;
		}
		requestLine = new RequestLine();
		requestLine.processRequestLine(line);

		line = bf.readLine();
		while (!line.equals("")) {
			log.debug("Header > {}", line);
			String[] token = line.split(":");
			headers.put(token[0].trim(), token[1].trim());

			line = bf.readLine();
		}

		if (getMethod().isPost()) {
			String body = readData(bf, Integer.parseInt(headers.get("Content-Length")));
			log.debug("body > {}", body);
			parameters = HttpRequestUtils.parseQueryString(body);
		} else {
			parameters = requestLine.getParameter();
		}

	}

	private String readData(BufferedReader br, int contentLength) throws IOException {
		char[] body = new char[contentLength];
		br.read(body, 0, contentLength);
		return String.copyValueOf(body);
	}

	public String getHeader(String headerKey) {
		return headers.get(headerKey);
	}

	public HttpMethod getMethod() {
		return requestLine.getMethod();
	}

	public String getPath() {
		return requestLine.getPath();
	}

	public String getParameter(String key) {
		return parameters.get(key);
	}
}
