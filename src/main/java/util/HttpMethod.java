package util;

/**
 * @author zack <zack@nhn.com>
 * @since 2018. 01. 04.
 */
public enum HttpMethod {
	POST,
	GET;

	public boolean isPost(){
		return this == POST;
	}
}
