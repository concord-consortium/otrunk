/**
 * 
 */
package org.concord.otrunk.view.prototype;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.concord.framework.otrunk.OTObject;

/**
 * @author scott
 *
 */
public class OTPrototypeProxyInvocationHandler implements InvocationHandler 
{
	// need the views template ot object, the passed in ot object (model)
	// and the mapping from the view template object to the passed in
	// object 
	OTObject viewTemplate;
	OTObject model;
	OTPrototypeProxyMapping view;	
		
	public OTPrototypeProxyInvocationHandler(OTObject viewTemplate,
			OTObject model,
			OTPrototypeProxyMapping view)
	{
		this.viewTemplate = viewTemplate;
		this.model = model;
		this.view = view;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// little hack for now
		if(method.getName().startsWith("get") && 
				(args == null || args.length == 0)){
			String propertyPart = method.getName().substring(4);
			String property = method.getName().substring(3,4).toLowerCase() +
				propertyPart;
			String translation = view.getProperty(property);
			if(translation != null){
				// get the method from the model class
				Class<?> modelClass = model.getClass();
				Method modelMethod = modelClass.getMethod("get" + 
						translation.substring(0,1).toUpperCase() +
						translation.substring(1));
				return modelMethod.invoke(model);
			}
		}

		if(method.getName().startsWith("set") && 
				(args != null && args.length == 1)){
			String propertyPart = method.getName().substring(4);
			String property = method.getName().substring(3,4).toLowerCase() +
				propertyPart;
			String translation = view.getProperty(property);
			if(translation != null){
				// get the method from the model class
				// we probably want to do some translation here
				// incase we want to display a text box for an int
				// really this is becoming a lot like script though
				// so perhaps it is best to replace this whole thing
				// dynamically generated javascript or beanshell.
				Class<?> modelClass = model.getClass();
				Method modelMethod = modelClass.getMethod("set" + 
						translation.substring(0,1).toUpperCase() +
						translation.substring(1), method.getParameterTypes());
				return modelMethod.invoke(model, args);
			}
		}
		
		
		// get the same method from the viewTemplate and call that
		Class<?> viewTemplateClass = viewTemplate.getClass();
		
		Method viewTemplateMethod = 
			viewTemplateClass.getMethod(method.getName(), method.getParameterTypes());
		
		//viewTemplateClass.
		return viewTemplateMethod.invoke(viewTemplate, args);
	}

}
