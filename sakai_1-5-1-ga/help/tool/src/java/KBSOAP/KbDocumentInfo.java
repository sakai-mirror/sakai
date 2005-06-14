/**
 * KbDocumentInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package KBSOAP;

public class KbDocumentInfo  implements java.io.Serializable {
    private int modifiedTime;
    private byte[] text;
    private java.lang.String title;
    private java.lang.String[] refDocuments;
    private java.lang.String[] refTitles;
    private java.lang.String[] docDomains;

    public KbDocumentInfo() {
    }

    public int getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(int modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public byte[] getText() {
        return text;
    }

    public void setText(byte[] text) {
        this.text = text;
    }

    public java.lang.String getTitle() {
        return title;
    }

    public void setTitle(java.lang.String title) {
        this.title = title;
    }

    public java.lang.String[] getRefDocuments() {
        return refDocuments;
    }

    public void setRefDocuments(java.lang.String[] refDocuments) {
        this.refDocuments = refDocuments;
    }

    public java.lang.String[] getRefTitles() {
        return refTitles;
    }

    public void setRefTitles(java.lang.String[] refTitles) {
        this.refTitles = refTitles;
    }

    public java.lang.String[] getDocDomains() {
        return docDomains;
    }

    public void setDocDomains(java.lang.String[] docDomains) {
        this.docDomains = docDomains;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof KbDocumentInfo)) return false;
        KbDocumentInfo other = (KbDocumentInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.modifiedTime == other.getModifiedTime() &&
            ((this.text==null && other.getText()==null) || 
             (this.text!=null &&
              java.util.Arrays.equals(this.text, other.getText()))) &&
            ((this.title==null && other.getTitle()==null) || 
             (this.title!=null &&
              this.title.equals(other.getTitle()))) &&
            ((this.refDocuments==null && other.getRefDocuments()==null) || 
             (this.refDocuments!=null &&
              java.util.Arrays.equals(this.refDocuments, other.getRefDocuments()))) &&
            ((this.refTitles==null && other.getRefTitles()==null) || 
             (this.refTitles!=null &&
              java.util.Arrays.equals(this.refTitles, other.getRefTitles()))) &&
            ((this.docDomains==null && other.getDocDomains()==null) || 
             (this.docDomains!=null &&
              java.util.Arrays.equals(this.docDomains, other.getDocDomains())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getModifiedTime();
        if (getText() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getText());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getText(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getTitle() != null) {
            _hashCode += getTitle().hashCode();
        }
        if (getRefDocuments() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRefDocuments());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRefDocuments(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRefTitles() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRefTitles());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRefTitles(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDocDomains() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDocDomains());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDocDomains(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(KbDocumentInfo.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:KBSOAP", "kbDocumentInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("modifiedTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "modifiedTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("text");
        elemField.setXmlName(new javax.xml.namespace.QName("", "text"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("title");
        elemField.setXmlName(new javax.xml.namespace.QName("", "title"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("refDocuments");
        elemField.setXmlName(new javax.xml.namespace.QName("", "refDocuments"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("refTitles");
        elemField.setXmlName(new javax.xml.namespace.QName("", "refTitles"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("docDomains");
        elemField.setXmlName(new javax.xml.namespace.QName("", "docDomains"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
