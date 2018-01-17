package webserver;

import util.HttpMethod;

/**
 * @author zack <zack@nhn.com>
 * @since 2018. 01. 07.
 */
abstract class AbstractController implements Controller {
	@Override
	public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
		HttpMethod httpMethod = httpRequest.getMethod();

		if (httpMethod.isPost()) {
			doPost(httpRequest, httpResponse);
		} else {
			doGet(httpRequest, httpResponse);
		}

	}

	protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {

	}

	protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {

	}
}
