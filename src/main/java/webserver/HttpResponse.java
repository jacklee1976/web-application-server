package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.Key;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zack <zack@nhn.com>
 * @since 2018. 01. 06.
 */
public class HttpResponse {
	private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
	private DataOutputStream dos;

	private Map<String, String> header = new HashMap<String, String>();

	public HttpResponse(OutputStream outputStream) {
		this.dos = new DataOutputStream(outputStream);

	}

	public void addHeaders(String key, String value) {
		header.put(key, value);
	}

	public void forward(String url) throws IOException {
		byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());

		if(url.endsWith(".css")){
			header.put("Content-Type", "text/css");
		}else if(url.endsWith(".js")){
			header.put("Content-Type", "appliction/javascript");
		}else {
			header.put("Content-Type", "text/html;charset=utf-8");
		}

		header.put("Content-Length", body.length + "");
		response200Header(body.length);
		responseBody(body);
	}

	public void forwardBody(String body) {
		byte[] contents = body.getBytes();

		header.put("Content-Type", "text/html;charset=utf-8");
		header.put("Content-Length", contents.length + "");

		response200Header(contents.length);
		responseBody(contents);

	}

	public void response200Header(int length) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			processHeaders();
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error("response200Headers Error : {}", e);
		}
	}

	public void responseBody(byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public void sendRedirect(String redirectUri) {
		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			processHeaders();
			dos.writeBytes("Location: " + redirectUri + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}

	}

	public void processHeaders() throws IOException {
		try {
			Set<String>  keys = header.keySet();

			for(String key : keys){
				dos.writeBytes(key +":" + header.get(key) + "\r\n");
			}
		} catch ( IOException ie){
			log.error(ie.getMessage());
		}

	}
}
