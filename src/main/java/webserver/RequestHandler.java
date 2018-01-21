package webserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
			connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			HttpRequest httpRequest = new HttpRequest(in);
			HttpResponse httpResponse = new HttpResponse(out);

			if (httpRequest.getCookie().getCookie("JSESSIONID") == null) {
				httpResponse.addHeaders("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
			}

			Controller controller = RequestMapping.getController(httpRequest.getPath());

			if (controller == null) {
				String path = getDefaultPath(httpRequest.getPath());
				httpResponse.forward(path);
			} else {
				controller.service(httpRequest, httpResponse);
			}

		} catch (Exception e) {
			log.error("ERROR : {}", e);
		}
	}

	private String getDefaultPath(String path) {
		if (path.equals("/")) {
			return "/index.html";
		}

		return path;
	}

}
