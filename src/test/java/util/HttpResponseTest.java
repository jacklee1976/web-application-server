package util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

import org.junit.Test;

import webserver.HttpResponse;

/**
 * @author zack <zack@nhn.com>
 * @since 2018. 01. 06.
 */
public class HttpResponseTest {
	private String testDirectory = "./src/test/resources/";

	@Test
	public void respnseForward() throws Exception {
		// Http_Forward.txt 결과는 응답 body에 index.html이 포함되어야 한다.
		HttpResponse httpResponse = new HttpResponse(createOutputStream("Http_Forward.txt"));
		httpResponse.forward("/index.html");
	}

	@Test
	public void responseRedirect() throws Exception {
		HttpResponse httpResponse = new HttpResponse(createOutputStream("Http_Redirect.txt"));
		httpResponse.sendRedirect("/index.html");
	}

	@Test
	public void responseCookies() throws Exception {
		HttpResponse httpResponse = new HttpResponse(createOutputStream("Http_Cookie.txt"));
		httpResponse.addHeaders("Set-Cookie", "logined=ture");
		httpResponse.sendRedirect("/index.html");
	}



	private OutputStream createOutputStream(String filename) throws FileNotFoundException {
		return new FileOutputStream(new File(testDirectory + filename));
	}
}
