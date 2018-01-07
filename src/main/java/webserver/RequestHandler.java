package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;

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

			String path = httpRequest.getPath();
			log.debug("url : {}", path);

			if (path.equals("/user/create")) {
				User user = new User(httpRequest.getParameter("userId"), httpRequest.getParameter("password"), httpRequest.getParameter("name"), httpRequest.getParameter("email"));
				log.debug("UserId : {}", user);

				DataBase.addUser(user);

				httpResponse.sendRedirect("/index.html");
			} else if (path.equals("/user/login")) {
				User user = DataBase.findUserById(httpRequest.getParameter("userId"));

				if (user == null) {
					httpResponse.forward("/user/login_failed.html");
					return;
				}

				if (user.getPassword().equals(httpRequest.getParameter("password"))) {
					httpResponse.addHeaders("Set-Cookie", "logined=true");
					httpResponse.sendRedirect("/index.html");
				} else {
					httpResponse.forward("/user/login_failed.html");
				}
			} else if (path.equals("/user/list")) {
				if (!isLogin(httpRequest.getHeader("Cookie"))) {
					httpResponse.forward("/user/login.html");
				}
				httpResponse.forwardBody(makeUserList());
			} else if (path.endsWith(".css")) {
				httpResponse.forward(path);
			} else {
				httpResponse.forward(path);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private boolean isLogin(String readLine) {
		Map<String, String> cookies = HttpRequestUtils.parseCookies(readLine);
		String value = cookies.get("logined");

		if (value == null) {
			return false;
		}

		return Boolean.parseBoolean(value);
	}

	private String makeUserList(){
		Collection<User> users = DataBase.findAll();
		StringBuilder sb = new StringBuilder();
		sb.append("<table border ='1'>");

		for (User user : users) {
			sb.append("<tr>");
			sb.append("<td>" + user.getUserId() + "</td>");
			sb.append("<td>" + user.getName() + "</td>");
			sb.append("<td>" + user.getEmail() + "</td>");
			sb.append("</tr>");
		}

		sb.append("</table>");

		return sb.toString();
	}

}
