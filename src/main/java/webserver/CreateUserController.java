package webserver;

import db.DataBase;
import model.User;

/**
 * @author zack <zack@nhn.com>
 * @since 2018. 01. 07.
 */
public class CreateUserController extends AbstractController{
	@Override
	public void doPost(HttpRequest httpRequest, HttpResponse httpResponse){
		User user = new User(httpRequest.getParameter("userId"), httpRequest.getParameter("password"), httpRequest.getParameter("name"), httpRequest.getParameter("email"));
		DataBase.addUser(user);

		httpResponse.sendRedirect("/index.html");
	}
}
