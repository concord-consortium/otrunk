package org.concord.otrunk.xml;

import org.concord.framework.otrunk.otcore.OTEnum;
import org.concord.otrunk.datamodel.EnumResource;
import org.concord.otrunk.xml.XMLReferenceInfo.EnumType;

public class EnumTypeHandler extends PrimitiveResourceTypeHandler
{

	private OTEnum otEnum;

	public EnumTypeHandler(OTEnum otEnum)
    {
	    super(TypeService.ENUM, Enum.class);
	    this.otEnum = otEnum;
    }

	@Override
	protected Object handleElement(String value)
	    throws HandlerException
	{
		throw new IllegalStateException("This method isn't supported by the EnumTypeHandler");
	}
	
	protected Object handleElement(String value, XMLReferenceInfo resInfo)
	    throws HandlerException
	{
		try{
			 Integer integer = Integer.decode(value);
			 Object ret = otEnum.getValue(integer); 
			 if(ret != null){
				 if(resInfo != null){
					 resInfo.enumType = EnumType.INT;
				 }
				 return ret;
			 }
		} catch(NumberFormatException e) {
			
		}

		Object ret = otEnum.getValue(value.trim());
		if(ret != null){
			if(resInfo != null){
				resInfo.enumType = EnumType.STRING;
			}
			return ret;
		}
		
		return null;
	}

	@Override
	public Object handleElement(OTXMLElement element, String relativePath, 
		XMLDataObject parent, String propertyName)
	throws HandlerException
	{
		return handleElement(element.getTextTrim(), parent.getReferenceInfo(propertyName));
	}

	@Override
	public Object handleAttribute(String value, String name, XMLDataObject parent)
	throws HandlerException
	{
		return handleElement(value, parent.getReferenceInfo(name));
	}

}
