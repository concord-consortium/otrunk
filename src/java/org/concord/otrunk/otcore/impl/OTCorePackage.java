package org.concord.otrunk.otcore.impl;

import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.framework.otrunk.OTXMLString;
import org.concord.framework.otrunk.otcore.OTListType;
import org.concord.framework.otrunk.otcore.OTMapType;
import org.concord.framework.otrunk.otcore.OTPrimitiveType;
import org.concord.otrunk.xml.TypeService;

public class OTCorePackage
{
    public final static OTPrimitiveType BOOLEAN_TYPE = 
    	new OTPrimitiveTypeImpl(TypeService.BOOLEAN, Boolean.class);
    public final static OTPrimitiveType INTEGER_TYPE = 
    	new OTPrimitiveTypeImpl(TypeService.INTEGER, Integer.class);
    public final static OTPrimitiveType LONG_TYPE = 
    	new OTPrimitiveTypeImpl(TypeService.LONG, Long.class);
    public final static OTPrimitiveType FLOAT_TYPE = 
    	new OTPrimitiveTypeImpl(TypeService.FLOAT, Float.class);
    public final static OTPrimitiveType DOUBLE_TYPE = 
    	new OTPrimitiveTypeImpl(TypeService.BOOLEAN, Double.class);
    public final static OTPrimitiveType BLOB_TYPE = 
    	new OTPrimitiveTypeImpl(TypeService.BLOB, byte[].class);
    public final static OTPrimitiveType STRING_TYPE = 
    	new OTPrimitiveTypeImpl(TypeService.STRING, String.class);
    public final static OTPrimitiveType XML_STRING_TYPE = 
    	new OTPrimitiveTypeImpl(TypeService.XML_STRING, OTXMLString.class);
    
    
    public final static OTListType OBJECT_LIST_TYPE = 
    	new OTListTypeImpl(OTObjectList.class);
    public final static OTListType RESOURCE_LIST_TYPE =
    	new OTListTypeImpl(OTResourceList.class);
    
    public final static OTMapType OBJECT_MAP_TYPE =
    	new OTMapTypeImpl(OTObjectMap.class);
    public final static OTMapType RESOURCE_MAP_TYPE =
    	new OTMapTypeImpl(OTResourceMap.class);
}
