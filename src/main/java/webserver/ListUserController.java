package webserver;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.HttpSession;

/**
 * @author zack <zack@nhn.com>
 * @since 2018. 01. 07.
 */
public class ListUserController extends AbstractController {
	@Override
	public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
		if (!isLogin(httpRequest.getSession())) {
			httpResponse.forward("/user/login.html");
		}
		httpResponse.forwardBody(makeUserList());
	}

	private boolean isLogin(HttpSession httpSession) {
		Object user = httpSession.getAttribute("user");

		if (user == null) {
			return false;
		}

		return true;
	}

	private String makeUserList() {
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
