/**
 * DosearchResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package KBSOAP;

public class DosearchResponse  implements java.io.Serializable {
    private java.lang.String query;
    private java.lang.String noExist;
    private float version;
    private java.lang.String advanced;
    private java.lang.String ignored;
    private java.lang.String[] documents;
    private java.lang.String[][] domains;
    private float[] scores;
    private java.lang.String[] titles;

    public DosearchResponse() {
    }

    public java.lang.String getQuery() {
        return query;
    }

    public void setQuery(java.lang.String query) {
        this.query = query;
    }

    public java.lang.String getNoExist() {
        return noExist;
    }

    public void setNoExist(java.lang.String noExist) {
        this.noExist = noExist;
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }

    public java.lang.String getAdvanced() {
        return advanced;
    }

    public void setAdvanced(java.lang.String advanced) {
        this.advanced = advanced;
    }

    public java.lang.String getIgnored() {
        return ignored;
    }

    public void setIgnored(java.lang.String ignored) {
        this.ignored = ignored;
    }

    public java.lang.String[] getDocuments() {
        return documents;
    }

    public void setDocuments(java.lang.String[] documents) {
        this.documents = documents;
    }

    public java.lang.String[][] getDomains() {
        return domains;
    }

    public void setDomains(java.lang.String[][] domains) {
        this.domains = domains;
    }

    public float[] getScores() {
        return scores;
    }

    public void setScores(float[] scores) {
        this.scores = scores;
    }

    public java.lang.String[] getTitles() {
        return titles;
    }

    public void setTitles(java.lang.String[] titles) {
        this.titles = titles;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DosearchResponse)) return false;
        DosearchResponse other = (DosearchResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.query==null && other.getQuery()==null) || 
             (this.query!=null &&
              this.query.equals(other.getQuery()))) &&
            ((this.noExist==null && other.getNoExist()==null) || 
             (this.noExist!=null &&
              this.noExist.equals(other.getNoExist()))) &&
            this.version == other.getVersion() &&
            ((this.advanced==null && other.getAdvanced()==null) || 
             (this.advanced!=null &&
              this.advanced.equals(other.getAdvanced()))) &&
            ((this.ignored==null && other.getIgnored()==null) || 
             (this.ignored!=null &&
              this.ignored.equals(other.getIgnored()))) &&
            ((this.documents==null && other.getDocuments()==null) || 
             (this.documents!=null &&
              java.util.Arrays.equals(this.documents, other.getDocuments()))) &&
            ((this.domains==null && other.getDomains()==null) || 
             (this.domains!=null &&
              java.util.Arrays.equals(this.domains, other.getDomains()))) &&
            ((this.scores==null && other.getScores()==null) || 
             (this.scores!=null &&
              java.util.Arrays.equals(this.scores, other.getScores()))) &&
            ((this.titles==null && other.getTitles()==null) || 
             (this.titles!=null &&
              java.util.Arrays.equals(this.titles, other.getTitles())));
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
        if (getQuery() != null) {
            _hashCode += getQuery().hashCode();
        }
        if (getNoExist() != null) {
            _hashCode += getNoExist().hashCode();
        }
        _hashCode += new Float(getVersion()).hashCode();
        if (getAdvanced() != null) {
            _hashCode += getAdvanced().hashCode();
        }
        if (getIgnored() != null) {
            _hashCode += getIgnored().hashCode();
        }
        if (getDocuments() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDocuments());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDocuments(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDomains() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDomains());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDomains(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getScores() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getScores());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getScores(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getTitles() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTitles());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTitles(), i);
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
        new org.apache.axis.description.TypeDesc(DosearchResponse.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:KBSOAP", "dosearchResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("query");
        elemField.setXmlName(new javax.xml.namespace.QName("", "query"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("noExist");
        elemField.setXmlName(new javax.xml.namespace.QName("", "noExist"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("version");
        elemField.setXmlName(new javax.xml.namespace.QName("", "version"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("advanced");
        elemField.setXmlName(new javax.xml.namespace.QName("", "advanced"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ignored");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ignored"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("documents");
        elemField.setXmlName(new javax.xml.namespace.QName("", "documents"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("domains");
        elemField.setXmlName(new javax.xml.namespace.QName("", "domains"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:KBSOAP", "kbstringlist"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scores");
        elemField.setXmlName(new javax.xml.namespace.QName("", "scores"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("titles");
        elemField.setXmlName(new javax.xml.namespace.QName("", "titles"));
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
