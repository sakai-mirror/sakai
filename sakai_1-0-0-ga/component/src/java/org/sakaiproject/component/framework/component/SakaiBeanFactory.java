/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/component/framework/component/SakaiBeanFactory.java,v 1.9 2004/06/22 03:10:09 ggolden Exp $
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

package org.sakaiproject.component.framework.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * <p>SakaiBeanFactory provides a bi-directional nestable bean factory.</p>
 * <p>Spring's <strong>DefaultListableBeanFactory</strong> supports nested factories, but only
 * extends a factory's bean definition root-wise; if the bean is not defined in a factory the parent
 * is checked.</p>
 * <p>This extends this idea to also search leaf-wise; if the bean is not defined in a factory or it's parent,
 * check the children.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.9 $
 */
public class SakaiBeanFactory extends DefaultListableBeanFactory
{
	/** The child component managers. */
	protected Set m_children = new HashSet();

	/** The current parent - we break the super's parent link so it's not used root-wise when searching. */
	protected BeanFactory m_parent = null;

	/**
	 * Cover DefaultListableBeanFactory no arg constructor.
	 */
	public SakaiBeanFactory()
	{
		super();
	}

	/**
	 * Cover DefaultListableBeanFactory w/parent constructor.
	 * @param parentBeanFactory
	 */
	public SakaiBeanFactory(BeanFactory parentBeanFactory)
	{
		// without the parent
		super();

		// remember the parent
		m_parent = parentBeanFactory;

		// link parent to child
		if (parentBeanFactory instanceof SakaiBeanFactory)
		{
			((SakaiBeanFactory) parentBeanFactory).setChild(this);
		}
	}

	public void setParentBeanFactory(BeanFactory parentBeanFactory)
	{
		// if the prior parent keeps track of children, inform it of its loss
		if ((m_parent != null) && (m_parent instanceof SakaiBeanFactory))
		{
			((SakaiBeanFactory) m_parent).removeChild(this);
		}

		// link parent to child
		if (parentBeanFactory instanceof SakaiBeanFactory)
		{
			((SakaiBeanFactory) parentBeanFactory).setChild(this);
		}

		// remeber here, with no parent set in the super
		m_parent = parentBeanFactory;
	}

	/**
	 * Set another child bean factory that calls this one parent.
	 * @param child The child bean factory.
	 */
	public void setChild(SakaiBeanFactory child)
	{
		m_children.add(child);
	}

	/**
	 * Remove this child bean factory from this one parent.
	 * @param child The child bean factory.
	 */
	public void removeChild(SakaiBeanFactory child)
	{
		m_children.remove(child);
	}

	/**
	 * Return the bean with the given name,
	 * checking the parent bean factory if not found.
	 * @param name name of the bean to retrieve
	 */
	public Object getBean(String name) throws BeansException
	{
		// move this call to the root
		if (m_parent != null)
		{
			return m_parent.getBean(name);
		}
		else
		{
			return getBeanDown(name);
		}
	}

	protected Object getBeanDown(String name) throws BeansException
	{
		try
		{
			// if I have it, return it
			return super.getBean(name);
		}

		catch (NoSuchBeanDefinitionException e)
		{
			// if not found, try my children
			for (Iterator iChildren = m_children.iterator(); iChildren.hasNext();)
			{
				SakaiBeanFactory child = (SakaiBeanFactory) iChildren.next();
				try
				{
					// if a child has it, return it
					return child.getBeanDown(name);
				}
				catch (NoSuchBeanDefinitionException ignore)
				{
				}
			}

			throw e;
		}
	}

	public boolean containsBean(String name)
	{
		// move this call to the root
		if (m_parent != null)
		{
			return m_parent.containsBean(name);
		}
		else
		{
			return containsBeanDown(name);
		}
	}

	protected boolean containsBeanDown(String name)
	{
		// if I have it, return it
		if (super.containsBean(name))
		{
			return true;
		}

		// if not found, try my children
		for (Iterator iChildren = m_children.iterator(); iChildren.hasNext();)
		{
			SakaiBeanFactory child = (SakaiBeanFactory) iChildren.next();

			// if a child has it, return it
			if (child.containsBeanDown(name))
			{
				return true;
			}
		}

		// if nobody has it, throw
		return false;
	}

	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException
	{
		// move this call to the root
		if (m_parent != null)
		{
			return m_parent.isSingleton(name);
		}
		else
		{
			return isSingletonDown(name);
		}
	}

	protected boolean isSingletonDown(String name) throws NoSuchBeanDefinitionException
	{
		try
		{
			// if I have it, return it
			return super.isSingleton(name);
		}

		catch (NoSuchBeanDefinitionException e)
		{
			// if not found, try my children
			for (Iterator iChildren = m_children.iterator(); iChildren.hasNext();)
			{
				SakaiBeanFactory child = (SakaiBeanFactory) iChildren.next();
				try
				{
					// if a child has it, return it
					return child.isSingletonDown(name);
				}
				catch (NoSuchBeanDefinitionException ignore)
				{
				}
			}

			throw e;
		}
	}

	public String[] getAliases(String name) throws NoSuchBeanDefinitionException
	{
		// move this call to the root
		if (m_parent != null)
		{
			return m_parent.getAliases(name);
		}
		else
		{
			return getAliasesDown(name);
		}
	}

	protected String[] getAliasesDown(String name) throws NoSuchBeanDefinitionException
	{
		try
		{
			// if I have it, return it
			return super.getAliases(name);
		}

		catch (NoSuchBeanDefinitionException e)
		{
			// if not found, try my children
			for (Iterator iChildren = m_children.iterator(); iChildren.hasNext();)
			{
				SakaiBeanFactory child = (SakaiBeanFactory) iChildren.next();
				try
				{
					// if a child has it, return it
					return child.getAliasesDown(name);
				}
				catch (NoSuchBeanDefinitionException ignore)
				{
				}
			}

			throw e;
		}
	}

	/**
	 * Return a RootBeanDefinition, even by traversing parent if the parameter is a child definition.
	 * Will ask the parent bean factory if not found in this instance.
	 * @return a merged RootBeanDefinition with overridden properties
	 */
	public RootBeanDefinition getMergedBeanDefinition(String beanName, boolean includingAncestors) throws BeansException
	{
		// if not including "ancestors", we will get stuck at one level, so don't go up to the root
		if (!includingAncestors || (m_parent == null))
		{
			return getMergedBeanDefinitionDown(beanName, includingAncestors);
		}

		// move this call to the root
		else
		{
			return ((DefaultListableBeanFactory) m_parent).getMergedBeanDefinition(beanName, includingAncestors);
		}
	}

	protected RootBeanDefinition getMergedBeanDefinitionDown(String beanName, boolean includingAncestors)
		throws BeansException
	{
		try
		{
			// if I have it, return it
			return super.getMergedBeanDefinition(beanName, includingAncestors);
		}

		catch (NoSuchBeanDefinitionException e)
		{
			if (includingAncestors)
			{
				// if not found, try my children
				for (Iterator iChildren = m_children.iterator(); iChildren.hasNext();)
				{
					SakaiBeanFactory child = (SakaiBeanFactory) iChildren.next();
					try
					{
						// if a child has it, return it
						return child.getMergedBeanDefinitionDown(beanName, includingAncestors);
					}
					catch (NoSuchBeanDefinitionException ignore)
					{
					}
				}
			}

			throw e;
		}
	}

	public int getBeanDefinitionCount()
	{
		// move this call to the root
		if (m_parent != null)
		{
			return ((DefaultListableBeanFactory) m_parent).getBeanDefinitionCount();
		}
		else
		{
			return getBeanDefinitionCountDown();
		}
	}

	protected int getBeanDefinitionCountDown()
	{
		int size = super.getBeanDefinitionCount();
		for (Iterator iChildren = m_children.iterator(); iChildren.hasNext();)
		{
			SakaiBeanFactory child = (SakaiBeanFactory) iChildren.next();
			size += child.getBeanDefinitionCountDown();
		}
		return size;
	}

	public String[] getLocalBeanDefinitionNames()
	{
		return getBeanDefinitionNamesDown(false);
	}

	public String[] getBeanDefinitionNames()
	{
		// move this call to the root
		if (m_parent != null)
		{
			return ((DefaultListableBeanFactory) m_parent).getBeanDefinitionNames();
		}
		else
		{
			return getBeanDefinitionNamesDown(true);
		}
	}

	protected String[] getBeanDefinitionNamesDown(boolean children)
	{
		List rv = new Vector();
		String[] names = super.getBeanDefinitionNames();
		rv.addAll(Arrays.asList(names));

		if (children)
		{
			for (Iterator iChildren = m_children.iterator(); iChildren.hasNext();)
			{
				SakaiBeanFactory child = (SakaiBeanFactory) iChildren.next();
				names = child.getBeanDefinitionNamesDown(children);
				rv.addAll(Arrays.asList(names));
			}
		}
		return (String[]) rv.toArray(new String[rv.size()]);
	}

	public Map getBeansOfType(Class type, boolean includePrototypes, boolean includeFactoryBeans) throws BeansException
	{
		// move this call to the root
		if (m_parent != null)
		{
			return ((DefaultListableBeanFactory) m_parent).getBeansOfType(type, includePrototypes, includeFactoryBeans);
		}
		else
		{
			return getBeansOfTypeDown(type, includePrototypes, includeFactoryBeans);
		}
	}

	protected Map getBeansOfTypeDown(Class type, boolean includePrototypes, boolean includeFactoryBeans)
		throws BeansException
	{
		Map rv = super.getBeansOfType(type, includePrototypes, includeFactoryBeans);

		// add my children
		for (Iterator iChildren = m_children.iterator(); iChildren.hasNext();)
		{
			SakaiBeanFactory child = (SakaiBeanFactory) iChildren.next();

			rv.putAll(child.getBeansOfTypeDown(type, includePrototypes, includeFactoryBeans));
		}

		return rv;
	}

	Set m_destroyBeanInProgress = new HashSet();

	protected void destroyBean(String beanName, Object bean)
	{
		synchronized (m_destroyBeanInProgress)
		{
			// we are already doing this one, so move on
			if (m_destroyBeanInProgress.contains(beanName))
			{
				System.out.println("SakaiBeanFactory.destroyBean: recursive definition involving: " + beanName);
				return;
			}
			// we are working on this one
			m_destroyBeanInProgress.add(beanName);
		}

		// move this call to the root
		if (m_parent != null)
		{
			((SakaiBeanFactory) m_parent).destroyBean(beanName, bean);
		}
		else
		{
			destroyBeanDown(beanName, bean);
		}

		synchronized (m_destroyBeanInProgress)
		{
			// we are done working on this one
			m_destroyBeanInProgress.remove(beanName);
		}
	}

	protected void destroyBeanDown(String beanName, Object bean)
	{
		// destroy it here, if present
		super.destroyBean(beanName, bean);

		// try my children
		for (Iterator iChildren = m_children.iterator(); iChildren.hasNext();)
		{
			SakaiBeanFactory child = (SakaiBeanFactory) iChildren.next();

			// destroy it here, if present
			child.destroyBeanDown(beanName, bean);
		}
	}

	protected String[] getDependingBeanNames(String beanName) throws BeansException
	{
		List dependingBeanNames = new ArrayList();
		String[] beanDefinitionNames = getBeanDefinitionNames();
		for (int i = 0; i < beanDefinitionNames.length; i++)
		{
			try
			{
				List dependsOn = new Vector();
				RootBeanDefinition bd = getMergedBeanDefinition(beanDefinitionNames[i], true);

				// consider the special depends on
				if (bd.getDependsOn() != null)
				{
					dependsOn.addAll(Arrays.asList(bd.getDependsOn()));
				}

				// and all the property values which are runtime bean references
				PropertyValue[] propValues = bd.getPropertyValues().getPropertyValues();
				if (propValues != null)
				{
					for (int v = 0; v < propValues.length; v++)
					{
						if (propValues[v].getValue() instanceof RuntimeBeanReference)
						{
							dependsOn.add(((RuntimeBeanReference) propValues[v].getValue()).getBeanName());
						}
					}
				}

				if (dependsOn.contains(beanName))
				{
					logger.debug("Found depending bean '" + beanDefinitionNames[i] + "' for bean '" + beanName + "'");
					dependingBeanNames.add(beanDefinitionNames[i]);
				}
			}
			catch (Throwable t)
			{
			}
		}
		String[] rv = (String[]) dependingBeanNames.toArray(new String[dependingBeanNames.size()]);
//System.out.print(" *** depending beans for: " + beanName + " : ");
//for (int i = 0; i < rv.length; i++)
//{
//	System.out.print(rv[i] + " ");
//}
//System.out.println();
		return rv;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/component/framework/component/SakaiBeanFactory.java,v 1.9 2004/06/22 03:10:09 ggolden Exp $
*
**********************************************************************************/
