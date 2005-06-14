/**
 * KBSOAPServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package KBSOAP;

public class KBSOAPServiceLocator extends org.apache.axis.client.Service implements KBSOAP.KBSOAPService {

    // Use to get a proxy class for KBSOAPPort
    private final java.lang.String KBSOAPPort_address = "http://kb.indiana.edu/soap.cgi";

    public java.lang.String getKBSOAPPortAddress() {
        return KBSOAPPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String KBSOAPPortWSDDServiceName = "KBSOAPPort";

    public java.lang.String getKBSOAPPortWSDDServiceName() {
        return KBSOAPPortWSDDServiceName;
    }

    public void setKBSOAPPortWSDDServiceName(java.lang.String name) {
        KBSOAPPortWSDDServiceName = name;
    }

    public KBSOAP.KBSOAPPort getKBSOAPPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(KBSOAPPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getKBSOAPPort(endpoint);
    }

    public KBSOAP.KBSOAPPort getKBSOAPPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            KBSOAP.KBSOAPBindingStub _stub = new KBSOAP.KBSOAPBindingStub(portAddress, this);
            _stub.setPortName(getKBSOAPPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (KBSOAP.KBSOAPPort.class.isAssignableFrom(serviceEndpointInterface)) {
                KBSOAP.KBSOAPBindingStub _stub = new KBSOAP.KBSOAPBindingStub(new java.net.URL(KBSOAPPort_address), this);
                _stub.setPortName(getKBSOAPPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("KBSOAPPort".equals(inputPortName)) {
            return getKBSOAPPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:KBSOAP", "KBSOAPService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("KBSOAPPort"));
        }
        return ports.iterator();
    }

}
