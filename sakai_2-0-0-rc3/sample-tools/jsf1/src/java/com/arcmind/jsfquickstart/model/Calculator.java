package com.arcmind.jsfquickstart.model;

/**
 * Calculator
 *
 * from http://www-106.ibm.com/developerworks/library/j-jsf1/
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class Calculator {
    //~ Methods ----------------------------------------------------------------

    /**
     * add numbers.
     *
     * @param a first number
     * @param b second number
     *
     * @return result
     */
    public int add(int a, int b) {
        return a + b;
    }

    /**
     * multiply numbers.
     *
     * @param a first number
     * @param b second number
     *
     * @return result
     */
    public int multiply(int a, int b) {
        return a * b;
    }

    /**
     * divide numbers.
     *
     * @param a first number
     * @param b second number
     *
     * @return result
     */
    public int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Divide by zero");
        }
        return a / b;
    }
}
