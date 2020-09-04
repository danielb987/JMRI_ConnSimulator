/*
 *  Copyright(c)1998 Forward Computing and Control Pty. Ltd.
 *  ACN 003 669 994, NSW, Australia     All rights Reserved
 *  
 *   Written by Dr. M.P. Ford
 */
package common;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *  Contains a useful static toString() method
 *  that handles both null's and Throwable's 
 *
 * @author     Dr. M.P. Ford 
 * @created    October 21, 2001 
 * @version    0.1 15th Jan 1998 
 */

public class StringUtils {

  /**
   *  Return toString of object handling null inputs
   *  if obj is a <code>Throwable</code> returns the stack trace as a <code>String</code>. 
   *
   * @param  obj  the Object to convert to a <code>String</code>. 
   * @return      "NULL" if obj is null, else 
   *              the contents of stack trace if obj is a Throwable else
   *              obj.toString()
   *              If obj.toString() throws an exception then that exception and its stack trace are returned
   */
	public static String getStackTrace(Object obj)
	{
		if (obj == null)
		{
			return "NULL";
		}

		Throwable e;
		if (obj instanceof Throwable)
		{
			e = (Throwable) obj;
			StringWriter strWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(strWriter);
			e.printStackTrace(printWriter);
			return strWriter.toString();
		} //else {
		try
		{
			return obj.toString();
		}
		catch (Throwable t)
		{
			return (getStackTrace(t));
		}

	}
	
}
