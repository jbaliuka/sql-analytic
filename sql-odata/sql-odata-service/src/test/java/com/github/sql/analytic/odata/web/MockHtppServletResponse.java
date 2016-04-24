package com.github.sql.analytic.odata.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;

public final class MockHtppServletResponse implements HttpServletResponse {
	
	private HttpResponse clientResponse;
	private ByteArrayOutputStream out = new ByteArrayOutputStream();

	public MockHtppServletResponse(HttpResponse clientResponse) {
		this.clientResponse = clientResponse;
	}

	@Override
	public String getCharacterEncoding() {
		
		return null;
	}

	@Override
	public String getContentType() {
		
		return null;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		
		return new ServletOutputStream(){

			@Override
			public boolean isReady() {				
				return true;
			}

			@Override
			public void setWriteListener(WriteListener writeListener) {
				
				
			}

			@Override
			public void write(int b) throws IOException {
				out.write(b);				
			}
			
		};
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		
		return new PrintWriter(new OutputStreamWriter(getOutputStream()));
	}

	@Override
	public void setCharacterEncoding(String charset) {
		
		
	}

	@Override
	public void setContentLength(int len) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContentLengthLong(long len) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContentType(String type) {
		
		
	}

	@Override
	public void setBufferSize(int size) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getBufferSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void flushBuffer() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetBuffer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCommitted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLocale(Locale loc) {
		clientResponse.setLocale(loc);
		
	}

	@Override
	public Locale getLocale() {		
		return null;
	}

	@Override
	public void addCookie(Cookie cookie) {
		
		
	}

	@Override
	public boolean containsHeader(String name) {
		
		return false;
	}

	@Override
	public String encodeURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeUrl(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectUrl(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendError(int sc) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDateHeader(String name, long date) {
		clientResponse.addHeader(name, new Date(date).toGMTString() );
	}

	@Override
	public void addDateHeader(String name, long date) {
		clientResponse.addHeader(name, new Date(date).toGMTString() );
		
	}

	@Override
	public void setHeader(String name, String value) {
		clientResponse.addHeader(name, value );
		
	}

	@Override
	public void addHeader(String name, String value) {
		clientResponse.addHeader(name, value );
		
	}

	@Override
	public void setIntHeader(String name, int value) {
		clientResponse.addHeader(name, Integer.toString(value) );
		
	}

	@Override
	public void addIntHeader(String name, int value) {
		clientResponse.addHeader(name, Integer.toString(value) );
		
	}

	@Override
	public void setStatus(int sc) {
		clientResponse.setStatusCode(sc);
		
	}

	@Override
	public void setStatus(int sc, String sm) {
		clientResponse.setStatusCode(sc);
		
	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getHeader(String name) {		
		return null;
	}

	@Override
	public Collection<String> getHeaders(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpEntity getEntity() {
		
		return  new ByteArrayEntity(out.toByteArray());
	}
}