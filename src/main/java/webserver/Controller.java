package webserver;

/**
 * @author zack <zack@nhn.com>
 * @since 2018. 01. 07.
 */
public interface Controller {
	void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception;
}
