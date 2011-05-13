package org.concord.otrunk.xml;

public class XMLReferenceInfo
{
	public enum XmlType {
		ATTRIBUTE, ELEMENT
	}

	public enum EnumType {
		STRING, INT
	}
	
	public enum IntType {
		HEX, BASE10
	}
	
	/**
	 * This indicates if this resource was stored as a element or an attribute
	 */
	public XmlType xmlType = XmlType.ELEMENT;

	/**
	 * This is a comment that can be associated with this reference. When the
	 * reference is serialized it will be saved as a comment above the
	 * reference.
	 * 
	 * If the reference is a attribute it will not be saved.
	 */
	public String comment;
	
	/**
	 * If this is reference is an enumeration then this records how that enumeration was
	 * stored.
	 */
	public EnumType enumType = EnumType.STRING;
	
	/**
	 * If this is an integer value, record whether the value was represented as a hexadecimal or base-10 number
	 */
	public IntType intType = IntType.BASE10;
}
