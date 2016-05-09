package com.github.sql.analytic.odata;

import java.util.Map;

/**
 * Primitive Odata function adapter interface
 * Function implementation should not produce any side effects
 * 
 * @author mama
 *
 */
public interface FunctionCommand {
	/**
	 * Function name
	 * @return name
	 */
	String getName();
	/**
	 * Parameter types by name
	 * @return map
	 */
	Map<String,Class<?>> parameterTypes();
	Class<?> getReturnType();
	/**
	 * Executes function command
	 * @param parameters
	 * @return Odata compatible primitive
	 */
	Object execute(Map<String,Object> parameters);

}
