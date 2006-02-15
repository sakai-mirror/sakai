package uk.ac.cam.caret.sakai.rwiki.component.service.impl.testutil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class JUnitHttpServletResponse implements HttpServletResponse {

	public void addCookie(Cookie arg0) {
		// TODO Auto-generated method stub

	}

	public boolean containsHeader(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public String encodeURL(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String encodeRedirectURL(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String encodeUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String encodeRedirectUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void sendError(int arg0, String arg1) throws IOException {
		// TODO Auto-generated method stub

	}

	public void sendError(int arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	public void sendRedirect(String arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	public void setDateHeader(String arg0, long arg1) {
		// TODO Auto-generated method stub

	}

	public void addDateHeader(String arg0, long arg1) {
		// TODO Auto-generated method stub

	}

	public void setHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public void addHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public void setIntHeader(String arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	public void addIntHeader(String arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	public void setStatus(int arg0) {
		// TODO Auto-generated method stub

	}

	public void setStatus(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public String getCharacterEncoding() {
		return charEncoding;
	}

	private String contentType = null;
	public String getContentType() {
		return contentType;
	}

	private StringWriter stringOut = new StringWriter();
	private ServletOutputStream sout = new ServletOutputStream() {
		public void write(int arg0) throws IOException {
			stringOut.write(arg0);
		}				
	};
	
	public ServletOutputStream getOutputStream() throws IOException {
		return sout;
	}
	public void resetStream() {
		stringOut = new StringWriter();
	}
	public String getOutput() throws IOException {
		sout.flush();
		stringOut.flush();
		return stringOut.toString();
	}

	public PrintWriter getWriter() throws IOException {
		
		return new PrintWriter(stringOut);
	}

	private String charEncoding = "";
	public void setCharacterEncoding(String arg0) {
		charEncoding = arg0;
	}

	public void setContentLength(int arg0) {
	}

	public void setContentType(String arg0) {
		contentType = arg0;
	}

	int bufferSize =1024;
	public void setBufferSize(int arg0) {
		bufferSize = arg0;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void flushBuffer() throws IOException {
	}

	public void resetBuffer() {
	}

	public boolean isCommitted() {
		return false;
	}

	public void reset() {

	}

	private Locale locale = null;
	public void setLocale(Locale arg0) {
		locale = arg0;

	}

	public Locale getLocale() {
		return locale;
	}

}

