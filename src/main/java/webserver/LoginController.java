package webserver;

import java.io.IOException;

import db.DataBase;
import model.User;

/**
 * @author zack <zack@nhn.com>
 * @since 2018. 01. 07.
 */
public class LoginController extends AbstractController {
	@Override
	public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
		User user = DataBase.findUserById(httpRequest.getParameter("userId"));

		if (user == null) {
			httpResponse.forward("/user/login_failed.html");
			return;
		}

		if (user.getPassword().equals(httpRequest.getParameter("password"))) {
			httpRequest.getSession().setAttributes("user", user);
			httpResponse.sendRedirect("/index.html");
		} else {
			httpResponse.forward("/user/login_failed.html");
		}
	}
}
