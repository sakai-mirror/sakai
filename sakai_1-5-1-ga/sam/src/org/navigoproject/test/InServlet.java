package org.navigoproject.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.navigoproject.data.GenericConnectionManager;

/**
 * @author casong
 * @version $Id: InServlet.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class InServlet extends HttpServlet
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(InServlet.class);

  /**
   * Constructor of the object.
   */
  public InServlet()
  {
    super();
  }

  /**
   * Destruction of the servlet. <br>
   */
  public void destroy()
  {
    super.destroy(); // Just puts "destroy" string in log
    // Put your code here
  }

  /**
   * The doGet method of the servlet. <br>
   *
   * This method is called when a form has its tag value method equals to get.
   * 
   * @param request the request send by the client to the server
   * @param response the response send by the server to the client
   * @throws ServletException if an error occurred
   * @throws IOException if an error occurred
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {

    response.setContentType("text/html");
    //<meta HTTP-EQUIV="content-type" CONTENT="text/html; charset=UTF-8">
    PrintWriter out = response.getWriter();
    out.println(
      "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
    out.println("<HTML>");
    out.println("  <HEAD><meta HTTP-EQUIV=\"content-type\" CONTENT=\"text/html; charset=UTF-8\"><TITLE>A Servlet</TITLE></HEAD>");
    out.println("  <BODY>");
    out.print("    This is ");
    out.print(this.getClass());
    out.println(", using the GET method");
    ArrayList list = this.getData();
    for(int i=0; i<list.size(); i++)
    {
      out.println("<br>Test data: " + (String)list.get(i));
    }
    out.println("<form action=http://localhost:8080/Navigo/servlet/InServlet method=post>");
    out.println("<input type=text name=test>  <input type=submit value=add></form>");
    out.println("  </BODY>");
    out.println("</HTML>");
    out.flush();
    out.close();
  }

  /**
   * The doPost method of the servlet. <br>
   *
   * This method is called when a form has its tag value method equals to post.
   * 
   * @param request the request send by the client to the server
   * @param response the response send by the server to the client
   * @throws ServletException if an error occurred
   * @throws IOException if an error occurred
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    String input = request.getParameter("test");

    response.setContentType("text/html");
    //<meta HTTP-EQUIV="content-type" CONTENT="text/html; charset=UTF-8">
    PrintWriter out = response.getWriter();
    out.println(
      "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
    out.println("<HTML>");
    out.println("  <HEAD><meta HTTP-EQUIV=\"content-type\" CONTENT=\"text/html; charset=UTF-8\"><TITLE>A Servlet</TITLE></HEAD>");
    out.println("  <BODY>");
    out.print("    This is ");
    out.print(this.getClass());
    out.println(", using the GET method");
    try{
      boolean ok = this.saveToDB(input);
    }
    catch(Exception ex){
      out.println(ex.getMessage());
    }
    ArrayList list = this.getData();
    for(int i=0; i<list.size(); i++)
    {
      out.println("<br>Test data: " + (String)list.get(i));
    }
    out.println("<br>the new input is: " + input);
    out.println("<br><form action=http://localhost:8080/Navigo/servlet/InServlet method=post>");
    out.println("<input type=text name=test>  <input type=submit value=add></form>");
    out.println("  </BODY>");
    out.println("</HTML>");
    out.flush();
    out.close();
//    response.setContentType("text/html");
//    PrintWriter out = response.getWriter();
//    out.println(
//      "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
//    out.println("<HTML>");
//    out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
//    out.println("  <BODY>");
//    out.print("    This is ");
//    out.print(this.getClass());
//    out.println(", using the POST method");
//    out.println("  </BODY>");
//    out.println("</HTML>");
//    out.flush();
//    out.close();
  }

  /**
   * Initilaisation of the servlet. <br>
   *
   * @throws ServletException if an error occure
   */
  public void init() throws ServletException
  {
    // Put your code here
  }
  
  public ArrayList getData(){
    GenericConnectionManager gcm = GenericConnectionManager.getInstance();
    ArrayList list = new ArrayList();
    try
    {
      Connection conn = gcm.getConnection();
      PreparedStatement statement = conn.prepareStatement("select a from test");
      boolean ok = statement.execute();
      ResultSet results = statement.executeQuery();
      String s="";
      while(results.next())
      {
        s= results.getString("a");
        list.add(s);
      }
      results.close();
      conn.close();
    }
    catch (Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
    return list;
  }
  
  public boolean saveToDB(String input) throws Exception
  {
    GenericConnectionManager gcm = GenericConnectionManager.getInstance();
    boolean ok = true;
      Connection conn = gcm.getConnection();
      PreparedStatement statement = conn.prepareStatement("insert into test values (?)");
      statement.setString(1, input);
      ok = statement.execute();
      conn.close();

    return ok;
  }

}
