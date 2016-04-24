package com.github.sql.analytic.odata.web;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.ReadListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpUriRequest;

public final class MockHttpServletRequest implements HttpServletRequest {
	private HttpUriRequest clientRequest;

	public MockHttpServletRequest(HttpUriRequest clientRequest) {
		this.clientRequest = clientRequest;
	}

	@Override
	public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
			throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAttribute(String name, Object o) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAttribute(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAsyncSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAsyncStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ServletContext getServletContext() {

		return null;
	}

	@Override
	public int getServerPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getServerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getScheme() {		
		return clientRequest.getURI().getScheme();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRemotePort() {

		return 0;
	}

	@Override
	public String getRemoteHost() {

		return clientRequest.getURI().getHost();
	}

	@Override
	public String getRemoteAddr() {

		return clientRequest.getURI().toString();
	}

	@Override
	public String getRealPath(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferedReader getReader() throws IOException {

		return  new BufferedReader(null);
	}

	@Override
	public String getProtocol() {

		return "http";
	}

	@Override
	public String[] getParameterValues(String name) {		
		return new String[] { (String)clientRequest.getParams().getParameter(name) };
	}

	@Override
	public Enumeration<String> getParameterNames() {

		return null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParameter(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<Locale> getLocales() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {		


		return new ServletInputStream() {

			private InputStream in;

			{

				if(clientRequest instanceof HttpEntityEnclosingRequest ){
					in = ((HttpEntityEnclosingRequest) clientRequest).getEntity().getContent();
				}else {
					in = new ByteArrayInputStream(new byte[]{});
				}

			}

			@Override
			public int read() throws IOException {

				return in.read();
			}

			@Override
			public void setReadListener(ReadListener readListener) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isReady() {			
				return false;
			}

			@Override
			public boolean isFinished() {				
				return false;
			}
		};
	}

	@Override
	public DispatcherType getDispatcherType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getContentLengthLong() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getContentLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncContext getAsyncContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void logout() throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void login(String username, String password) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isUserInRole(String role) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServletPath() {		
		return "/SQLODataService.svc";
	}

	@Override
	public String getRequestedSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuffer getRequestURL() {
		
		try {
			return new StringBuffer(clientRequest.getURI().toURL().toString());
		} catch (MalformedURLException e) {
		  return new StringBuffer(clientRequest.getURI().toString());
		}
	}

	@Override
	public String getRequestURI() {
		
		return clientRequest.getURI().toString();
	}

	@Override
	public String getRemoteUser() {		
		return null;
	}

	@Override
	public String getQueryString() {		
		return clientRequest.getURI().getQuery();
	}

	@Override
	public String getPathTranslated() {		
		return null;
	}

	@Override
	public String getPathInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Part getPart(String name) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMethod() {

		return clientRequest.getMethod();
	}

	@Override
	public int getIntHeader(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Enumeration<String> getHeaders(String name) {

		Collection<String> list = new ArrayList<>();
		for( Header e: clientRequest.getAllHeaders()){
			if(name.equals(e.getName())){
				list.add(e.getValue());
			}
		}

		return Collections.enumeration(list );
	}

	@Override
	public Enumeration<String> getHeaderNames() {		
		Collection<String> list = new ArrayList<>();
		for( Header e: clientRequest.getAllHeaders()){
			list.add(e.getName());
		}

		return Collections.enumeration(list );
	}

	@Override
	public String getHeader(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getDateHeader(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Cookie[] getCookies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContextPath() {		
		return "";
	}

	@Override
	public String getAuthType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String changeSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return false;
	}
}