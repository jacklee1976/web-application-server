package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import javax.xml.crypto.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	private static final String EMPTY_STRING = "";
	private static final String HTTP_REQUEST_SPLITER = " ";

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
			connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			HttpRequest httpRequest = new HttpRequest(in);

			String path = httpRequest.getPath();
			log.debug("url : {}", path);

			if (path.equals("/user/create")) {
				User user = new User(httpRequest.getParameter("userId"), httpRequest.getParameter("password"), httpRequest.getParameter("name"), httpRequest.getParameter("email"));
				log.debug("UserId : {}", user);

				DataBase.addUser(user);

				DataOutputStream dos = new DataOutputStream(out);
				response302Header(dos, "/index.html");
			} else if (path.equals("/user/login")) {
				User user = DataBase.findUserById(httpRequest.getParameter("userId"));

				if (user == null) {
					responseResource(out, "/user/login_failed.html");
					return;
				}

				if (user.getPassword().equals(httpRequest.getParameter("password"))) {
					DataOutputStream dos = new DataOutputStream(out);
					response302LoginSucessHeader(dos);

				} else {
					responseResource(out, "/user/login_failed.html");
				}
			}else if(path.equals("/user/list")) {
				if (!isLogin(httpRequest.getHeader("Cookie"))) {
					responseResource(out, "/user/login.html");
				}

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

				byte[] body = sb.toString().getBytes();
				DataOutputStream dos = new DataOutputStream(out);

				response200Header(dos, body.length);
				responseBody(dos, body);
			} else if(path.endsWith(".css")){
				DataOutputStream dos = new DataOutputStream(out);
				byte[] body = Files.readAllBytes(new File("./webapp"+ path).toPath());

				response200CssHeader(dos, body.length);
				responseBody(dos, body);
			} else {
				responseResource(out, path);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private boolean isLogin(String readLine) {
		Map<String, String> cookies = HttpRequestUtils.parseCookies(readLine);
		String value = cookies.get("logined");

		if(value == null){
			return  false;
		}

		return Boolean.parseBoolean(value);
	}

	private void responseResource(OutputStream out, String url) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);

		byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
		response200Header(dos, body.length);
		responseBody(dos, body);
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/css\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response302Header(DataOutputStream dos, String url) {
		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Location: " + url + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response302LoginSucessHeader(DataOutputStream dos) {
		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Set-Cookie: logined=true \r\n");
			dos.writeBytes("Location: /index.html \r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public static String[] getSplit(String splitTarget) {
		return splitTarget.split(" ");
	}

	public static void main(String[] args) {
		String[] test = getSplit("GET /index.html HTTP/1.1");
		log.debug("prefix : {}", test[0]);
		log.debug("contents : {}", test[1]);

		log.debug("true is : {}", test[0].toString().equals("GET") && test[1].toString().equals("/index.html"));

	}
}
