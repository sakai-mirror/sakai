package com.arcmind.jsfquickstart.controller;

import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.arcmind.jsfquickstart.model.Calculator;

/**
 * Calculator Controller from http://www-106.ibm.com/developerworks/library/j-jsf1/
 * 
 * @author $author$
 * @version $Revision$
 */
public class CalculatorConroller
{
	//~ Instance fields --------------------------------------------------------

	/**
	 * Repressent the model object.
	 */
	private Calculator calculator = new Calculator();

	/** First number used in operation. */
	private int firstNumber = 0;

	/** Result of operation on first number and second number. */
	private int result = 0;

	/** Second number used in operation. */
	private int secondNumber = 0;

	//~ Constructors -----------------------------------------------------------

	/**
	 * Creates a new CalculatorConroller object.
	 */
	public CalculatorConroller()
	{
		super();
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * Calculator, this class represent the model.
	 * 
	 * @param aCalculator
	 *        The calculator to set.
	 */
	public void setCalculator(Calculator aCalculator)
	{
		this.calculator = aCalculator;
	}

	/**
	 * First Number property
	 * 
	 * @param aFirstNumber
	 *        first number
	 */
	public void setFirstNumber(int aFirstNumber)
	{
		this.firstNumber = aFirstNumber;
	}

	/**
	 * First number property
	 * 
	 * @return First number.
	 */
	public int getFirstNumber()
	{
		return firstNumber;
	}

	/**
	 * Result of the operation on the first two numbers.
	 * 
	 * @return Second Number.
	 */
	public int getResult()
	{
		return result;
	}

	/**
	 * Second number property
	 * 
	 * @param aSecondNumber
	 *        Second number.
	 */
	public void setSecondNumber(int aSecondNumber)
	{
		this.secondNumber = aSecondNumber;
	}

	/**
	 * Get second number.
	 * 
	 * @return Second number.
	 */
	public int getSecondNumber()
	{
		return secondNumber;
	}

	/**
	 * Adds the first number and second number together.
	 * 
	 * @return next logical outcome.
	 */
	public String add()
	{
		if ((firstNumber == 0) || (secondNumber == 0))
		{
			FacesContext context = FacesContext.getCurrentInstance();
			ResourceBundle bundle = ResourceBundle.getBundle("sample.messages", context.getViewRoot().getLocale());
			String msg = bundle.getString("no_zero");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg));
			return "trouble";
		}

		result = calculator.add(firstNumber, secondNumber);

		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Use your fingers!"));
		return "success";
	}

	/**
	 * Multiplies the first number and second number together.
	 * 
	 * @return next logical outcome.
	 */
	public String multiply()
	{
		result = calculator.multiply(firstNumber, secondNumber);

		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle("sample.messages", context.getViewRoot().getLocale());
		String msg = bundle.getString("multiply");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg));
		return "success";
	}

	public String again()
	{
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle("sample.messages", context.getViewRoot().getLocale());
		String msg = bundle.getString("again");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg));
		return "calculate";
	}
}