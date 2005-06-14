/**********************************************************************************
*
* $Header: /cvs/sakai/covers/src/java/Cover.java,v 1.28 2005/02/10 19:12:04 ggolden.umich.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
* 
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
* 
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * <p>Cover is a utility which generates the cover classes for the Sakai Service APIs.</p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.28 $
 */
public class Cover
{
	/** The root folder of the src, where the covers are generated. */
	private String root = "";

	/**
	 * Construct with a source code target root folder.
	 */
	public Cover(String root)
	{
		this.root = root;
	}

	/**
	 * Remove the package from the class name.
	 * @param cls The class
	 * @return The class name, minus package.
	 */
	private String shortName(Class cls)
	{
		String packageName = cls.getPackage().getName();
		String className = cls.getName();
		return className.substring(packageName.length() + 1);
	}

	/**
	 * Deal with classes which are arrays for return types and parameters.
	 * @param cls The class
	 * @return The class name, if it is not an array, or the array type followed by "[]" if it is an array.
	 */
	private String typeName(Class cls)
	{
		if (!cls.isArray())
			return cls.getName();

		// Note: handles only one level deep (i.e no arrays of arrays)
		return cls.getComponentType().getName() + "[]";
	}

	/**
	 * Open a file for writing, returning a PrintStream on the file.
	 * The file is in the proper place for this interface's cover.
	 * All necessary directories are created.
	 * Any existing file is replaced.
	 * @param iface The interface for which a cover file is needed.
	 * @return A PrintStream good for writing this cover.
	 * @throws IOException
	 */
	private PrintStream openFile(Class iface) throws IOException
	{
		String packageName = iface.getPackage().getName();
		String[] parts = packageName.split("\\.");
		StringBuffer buf = new StringBuffer(root);
		for (int i = 0; i < parts.length; i++)
		{
			buf.append("/" + parts[i]);
		}
		buf.append("/cover/");
		buf.append(shortName(iface));
		buf.append(".java");

		File file = new File(buf.toString());
		file.mkdirs();
		file.delete();
		file.createNewFile();
		PrintStream out = new PrintStream(new FileOutputStream(file));

		return out;
	}

	/**
	 * Generate the Sakai static cover for this interface.
	 * @param iface The interface to cover.
	 * @throws IOException
	 */
	public void generateCover(Class iface) throws IOException
	{
		PrintStream out = openFile(iface);

		out.println("/**********************************************************************************");
		out.println("*");
		out.println("* $" + "Header:" + " $");
		out.println("*");
		out.println("***********************************************************************************");
		out.println("*");
		out.println("* Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,");
		out.println("*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation");
		out.println("* ");
		out.println("* Licensed under the Educational Community License Version 1.0 (the \"License\");");
		out.println("* By obtaining, using and/or copying this Original Work, you agree that you have read,");
		out.println("* understand, and will comply with the terms and conditions of the Educational Community License.");
		out.println("* You may obtain a copy of the License at:");
		out.println("* ");
		out.println("*      http://cvs.sakaiproject.org/licenses/license_1_0.html");
		out.println("* ");
		out.println("* THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,");
		out.println("* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE");
		out.println("* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,");
		out.println("* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING ");
		out.println("* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.");
		out.println("*");
		out.println("**********************************************************************************/");
		out.println();

		out.println("package " + iface.getPackage().getName() + ".cover;");
		out.println();
		out.println("import org.sakaiproject.service.framework.component.cover.ComponentManager;");
		out.println();

		out.println("/**");
		out.println(
			"* <p>"
				+ shortName(iface)
				+ " is a static Cover for the {@link "
				+ iface.getName()
				+ " "
				+ shortName(iface)
				+ "};");
		out.println("* see that interface for usage details.</p>");
		out.println("* ");
		out.println("* @author University of Michigan, Sakai Software Development Team");
		out.println("* @version $" + "Revision:" + " $");
		out.println("*/");

		out.println("public class " + shortName(iface));
		out.println("{");

		out.println("\t/**");
		out.println("\t * Access the component instance: special cover only method.");
		out.println("\t * @return the component instance.");
		out.println("\t */");
		out.println("\tpublic static " + iface.getName() + " getInstance()");
		out.println("\t{");
		out.println("\t\tif (ComponentManager.CACHE_SINGLETONS)");
		out.println("\t\t{");
		out.println("\t\t\tif (m_instance == null) m_instance = (" + iface.getName() + ") ComponentManager.get(" + iface.getName() + ".class);");
		out.println("\t\t\treturn m_instance;");
		out.println("\t\t}");
		out.println("\t\telse");
		out.println("\t\t{");
		out.println("\t\t\treturn (" + iface.getName() + ") ComponentManager.get(" + iface.getName() + ".class);");
		out.println("\t\t}");
		out.println("\t}");
		out.println("\tprivate static " + iface.getName() + " m_instance = null;");
		out.println();
		
		Field[] fields = iface.getFields();
		for (int f = 0; f < fields.length; f++)
		{
			out.println(
				"\tpublic static "
					+ typeName(fields[f].getType())
					+ " "
					+ fields[f].getName()
					+ " = "
					+ iface.getName()
					+ "."
					+ fields[f].getName()
					+ ";");
		}

		if (fields.length > 0)
			out.println();

		Method[] methods = iface.getMethods();
		for (int i = 0; i < methods.length; i++)
		{
			if (i > 0)
				out.println();
			Class returnType = methods[i].getReturnType();
			Class exceptionTypes[] = methods[i].getExceptionTypes();
			Class parameterTypes[] = methods[i].getParameterTypes();

			out.print("\tpublic static " + typeName(returnType) + " " + methods[i].getName());

			out.print("(");
			for (int p = 0; p < parameterTypes.length; p++)
			{
				if (p > 0)
					out.print(", ");
				out.print(typeName(parameterTypes[p]) + " param" + p);
			}
			out.print(")");

			if (exceptionTypes.length > 0)
			{
				out.print(" throws ");
				for (int e = 0; e < exceptionTypes.length; e++)
				{
					if (e > 0)
						out.print(", ");
					out.print(exceptionTypes[e].getName());
				}
			}
			out.println();

			out.println("\t{");

			out.println("\t\t" + iface.getName() + " service = getInstance();");

			out.println("\t\tif (service == null)");
			out.print("\t\t\treturn");
			if (!returnType.getName().equals("void"))
			{
				if (returnType.getName().equals("boolean"))
				{
					out.print(" false");
				}
				else if (returnType.getName().equals("int") || returnType.getName().equals("long"))
				{
					out.print(" 0");
				}
				else
				{
					out.print(" null");
				}
			}
			out.println(";");
			out.println();

			out.print("\t\t");
			if (!returnType.getName().equals("void"))
			{
				out.print("return ");
			}
			out.print("service." + methods[i].getName() + "(");
			for (int p = 0; p < parameterTypes.length; p++)
			{
				if (p > 0)
					out.print(", ");
				out.print("param" + p);
			}
			out.println(");");

			out.println("\t}");
		}

		out.println("}");
		out.println();

		out.println("/**********************************************************************************");
		out.println("*");
		out.println("* $" + "Header:" + " $");
		out.println("*");
		out.println("**********************************************************************************/");

		out.flush();
		out.close();
	}

	/**
	 * Generate new covers for all the interfaces that need covering.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		String baseDir = "/Users/ggolden/dev/sakai/service/src/java";
		
		if (args.length > 0)
		{
			baseDir = args[0];
		}

		Cover cover = new Cover(baseDir);		
		
		if (args.length > 1)
		{
			for (int i=1; i<args.length; i++)
			{
				Class c;
				String className = args[i];
				try
				{
					c = Class.forName(className);
					cover.generateCover(c);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		

		// TODO: update this list when the list of covered services changes
//		cover.generateCover(DiscussionService.class);
//		cover.generateCover(AliasService.class);
//		cover.generateCover(AnnouncementService.class);
//		cover.generateCover(ArchiveService.class);
//		cover.generateCover(AssignmentService.class);
//		cover.generateCover(CalendarService.class);
//		cover.generateCover(ChatService.class);
//		cover.generateCover(ServerConfigurationService.class);
//		cover.generateCover(ClusterService.class);
//		cover.generateCover(ContentHostingService.class);
//		cover.generateCover(ContentTypeImageService.class);
//		cover.generateCover(CourierService.class);
//		cover.generateCover(CurrentService.class);
//		cover.generateCover(DigestService.class);
//		cover.generateCover(DiscussionService.class);
//		cover.generateCover(DissertationService.class);
//		cover.generateCover(MailArchiveService.class);
//		cover.generateCover(EmailService.class);
//		cover.generateCover(EventTrackingService.class);
//		cover.generateCover(IdService.class);
//		cover.generateCover(MemoryService.class);
//		cover.generateCover(NewsService.class);
//		cover.generateCover(NotificationService.class);
//		cover.generateCover(PortalService.class);
//		cover.generateCover(PreferencesService.class);
//		cover.generateCover(PresenceService.class);
//		cover.generateCover(RealmService.class);
//		cover.generateCover(SecurityService.class);
//		cover.generateCover(UsageSessionService.class);
//		cover.generateCover(SiteService.class);
//		cover.generateCover(SqlService.class);
//		cover.generateCover(UserDirectoryService.class);
//		cover.generateCover(TimeService.class);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/covers/src/java/Cover.java,v 1.28 2005/02/10 19:12:04 ggolden.umich.edu Exp $
*
**********************************************************************************/
