package org.concord.otrunk.view;

import java.util.ArrayList;
import java.util.HashMap;

import org.concord.framework.otrunk.view.OTView;
import org.concord.framework.otrunk.view.OTViewConversionService;
import org.concord.framework.otrunk.view.OTViewConverter;
import org.concord.framework.otrunk.view.OTViewFactory;

public class OTViewConversionServiceImpl
    implements OTViewConversionService
{
	HashMap<Class<?>, ArrayList<OTViewConverter>> map = 
		new HashMap<Class<?>, ArrayList<OTViewConverter>>();
	
	public void addConverter(OTViewConverter converter)
	{
		Class<?> toType = converter.getToType();
		ArrayList<OTViewConverter> converters = map.get(toType);
		if(converters == null){
			converters = new ArrayList<OTViewConverter>();
			map.put(toType, converters);
		}
		
		converters.add(converter);
	}

	protected OTViewConverter getConverter(OTView originalView, 
		Class<? extends OTView> outputClass){
		ArrayList<OTViewConverter> converters = map.get(outputClass);
		if(converters == null){
			return null;
		}
		
		for (OTViewConverter viewConverter : converters) {
	        if(viewConverter.getFromType().isInstance(originalView)){
	        	return viewConverter;
	        }
        }
		
		return null;
	}
	
	public boolean canConvert(OTView originalView, Class<? extends OTView> outputClass)
	{
		return getConverter(originalView, outputClass) != null;
	}

	@SuppressWarnings("unchecked")
    public <T extends OTView> T convert(OTView originalView, Class<T> outputClass,
    	OTViewFactory viewFactory, org.concord.framework.otrunk.view.OTViewEntry viewEntry)
	{
		OTViewConverter converter = getConverter(originalView, outputClass);
		if(converter == null){
			return null;
		}
		
		return (T) converter.convert(originalView, viewFactory, viewEntry);
	}

}
