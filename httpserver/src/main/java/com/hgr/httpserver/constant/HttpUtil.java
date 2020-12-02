package com.hgr.httpserver.constant;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {
	private static Map<String, Object> requestMap = new HashMap<>();

	/**
	 * 解析Request字节流.
	 * <p>
	 * 当前方法仅仅适合与HTTP1.1版本，因为该版本的客户端对Request格式有严格的标准。
	 * 
	 * @param in
	 * @throws IOException
	 */
	public static Map<String, Object> parseRequestStream(InputStream in) throws IOException {
		BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
		bufferedInputStream.mark(0);

		byte[] totalArray = new byte[1024];
		bufferedInputStream.read(totalArray);
		System.out.println();
		System.out.println(Thread.currentThread().getName() + " Total: ");
		System.out.println(new String(totalArray));
		bufferedInputStream.reset();

		// 解析request-line
		int request_line_crlf_index = -1;
		boolean request_line_find = false;

		while (bufferedInputStream.available() > 0) {
			request_line_crlf_index++;
			if (bufferedInputStream.read() == 13) {
				if (bufferedInputStream.available() > 0) {
					request_line_crlf_index++;
					if (bufferedInputStream.read() == 10) {
						request_line_find = true;
						break;
					}
				}
			}
		}

		if (request_line_find) {
			// 解析header
			int header_crlf_index = -1;
			if (bufferedInputStream.available() > 0) {
				header_crlf_index = parseHeader(request_line_crlf_index, bufferedInputStream);
			}

			// 将requestLine值解析放入到requestMap
			bufferedInputStream.reset();
			byte[] byteArray = new byte[request_line_crlf_index + 1];
			bufferedInputStream.read(byteArray);
			String requestLine = new String(byteArray);
			String[] requestLineArray = requestLine.split(" ");
			if (requestLineArray.length != 3) {
				throw new IOException(
						Thread.currentThread().getName() + " HttpRequest_requestLine format error/request_line格式错误");
			}
			requestMap.put(HttpConstant.HTTP_REQUEST_METHOD, requestLineArray[0]);
			requestMap.put(HttpConstant.HTTP_REQUEST_URI, requestLineArray[1]);
			requestMap.put(HttpConstant.HTTP_VERSION, requestLineArray[2]);

			if (header_crlf_index != -1) {
				byte[] allHeaderBuffer = new byte[header_crlf_index - request_line_crlf_index];
				bufferedInputStream.read(allHeaderBuffer);
				Map<String, String> headerMap = new HashMap<>();
				int off = 0;
				for (int i = 0; i < allHeaderBuffer.length; i++) {
					if (i + 1 < allHeaderBuffer.length) {
						if (allHeaderBuffer[i] == 13 && allHeaderBuffer[i + 1] == 10) {
							byte[] headerByte = Arrays.copyOfRange(allHeaderBuffer, off, i);
							int colon = -1;
							for (int x = 0; x < headerByte.length; x++) {
								if (headerByte[x] == 58) {
									colon = x;
									break;
								}
							}
							if (colon != -1) {
								String headerName =  new String(Arrays.copyOfRange(headerByte, 0, colon));
								String headerValue = new String(Arrays.copyOfRange(headerByte, colon + 1, headerByte.length));
								headerMap.put(headerName, headerValue);
							}

							i = i + 1; // 外部会 + 1去到下一个header或者结束循环
							off = i + 1; // 相当于for中的i++
						}
					}
				}
				System.out.println(headerMap);
			}

			// 根据Request Method 决定是否需要解析body
			if (bufferedInputStream.available() > 0
					&& !HttpConstant.HTTP_REQUEST_METHOD_GET.equals(requestMap.get(HttpConstant.HTTP_REQUEST_METHOD))) {

			}
		} else {
			throw new IOException(Thread.currentThread().getName() + " HttpRequest format error/没有找到request_line");
		}

		return requestMap;
	}

	private static int parseHeader(int request_line_crlf_index, BufferedInputStream bufferedInputStream)
			throws IOException {
		// 解析headers
		int header_crlf_index = request_line_crlf_index;

		boolean header_find = false;
		while (bufferedInputStream.available() > 0) {
			header_crlf_index++;
			if (bufferedInputStream.read() == 13) {
				if (bufferedInputStream.available() > 0) {
					header_crlf_index++;
					if (bufferedInputStream.read() == 10) {
						if (bufferedInputStream.available() > 0) {
							header_crlf_index++;
							if (bufferedInputStream.read() == 13) {
								if (bufferedInputStream.available() > 0) {
									header_crlf_index++;
									if (bufferedInputStream.read() == 10) {
										header_find = true;
										break;
									}
								}
							}
						}
					}
				}
			}
		}

		if (header_find) {
			return header_crlf_index;
		}

		return -1;
	}
}
