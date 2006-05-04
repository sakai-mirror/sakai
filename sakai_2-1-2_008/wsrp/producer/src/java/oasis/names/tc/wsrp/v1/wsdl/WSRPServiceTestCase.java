/**
 * WSRPServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Feb 28, 2005 (10:15:14 EST) WSDL2Java emitter.
 */

package oasis.names.tc.wsrp.v1.wsdl;

public class WSRPServiceTestCase extends junit.framework.TestCase {
    public WSRPServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testWSRPPortletManagementServiceWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPPortletManagementServiceAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1WSRPPortletManagementServiceGetPortletDescription() throws Exception {
        oasis.names.tc.wsrp.v1.bind.WSRP_v1_PortletManagement_Binding_SOAPStub binding;
        try {
            binding = (oasis.names.tc.wsrp.v1.bind.WSRP_v1_PortletManagement_Binding_SOAPStub)
                          new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPPortletManagementService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        try {
            oasis.names.tc.wsrp.v1.types.PortletDescriptionResponse value = null;
            value = binding.getPortletDescription(new oasis.names.tc.wsrp.v1.types.GetPortletDescription());
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidUserCategoryFault e1) {
            throw new junit.framework.AssertionFailedError("InvalidUserCategory Exception caught: " + e1);
        }
        catch (oasis.names.tc.wsrp.v1.types.InconsistentParametersFault e2) {
            throw new junit.framework.AssertionFailedError("InconsistentParameters Exception caught: " + e2);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidRegistrationFault e3) {
            throw new junit.framework.AssertionFailedError("InvalidRegistration Exception caught: " + e3);
        }
        catch (oasis.names.tc.wsrp.v1.types.OperationFailedFault e4) {
            throw new junit.framework.AssertionFailedError("OperationFailed Exception caught: " + e4);
        }
        catch (oasis.names.tc.wsrp.v1.types.MissingParametersFault e5) {
            throw new junit.framework.AssertionFailedError("MissingParameters Exception caught: " + e5);
        }
        catch (oasis.names.tc.wsrp.v1.types.AccessDeniedFault e6) {
            throw new junit.framework.AssertionFailedError("AccessDenied Exception caught: " + e6);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidHandleFault e7) {
            throw new junit.framework.AssertionFailedError("InvalidHandle Exception caught: " + e7);
        }
            // TBD - validate results
    }

    public void test2WSRPPortletManagementServiceClonePortlet() throws Exception {
        oasis.names.tc.wsrp.v1.bind.WSRP_v1_PortletManagement_Binding_SOAPStub binding;
        try {
            binding = (oasis.names.tc.wsrp.v1.bind.WSRP_v1_PortletManagement_Binding_SOAPStub)
                          new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPPortletManagementService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        try {
            oasis.names.tc.wsrp.v1.types.PortletContext value = null;
            value = binding.clonePortlet(new oasis.names.tc.wsrp.v1.types.ClonePortlet());
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidUserCategoryFault e1) {
            throw new junit.framework.AssertionFailedError("InvalidUserCategory Exception caught: " + e1);
        }
        catch (oasis.names.tc.wsrp.v1.types.InconsistentParametersFault e2) {
            throw new junit.framework.AssertionFailedError("InconsistentParameters Exception caught: " + e2);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidRegistrationFault e3) {
            throw new junit.framework.AssertionFailedError("InvalidRegistration Exception caught: " + e3);
        }
        catch (oasis.names.tc.wsrp.v1.types.OperationFailedFault e4) {
            throw new junit.framework.AssertionFailedError("OperationFailed Exception caught: " + e4);
        }
        catch (oasis.names.tc.wsrp.v1.types.MissingParametersFault e5) {
            throw new junit.framework.AssertionFailedError("MissingParameters Exception caught: " + e5);
        }
        catch (oasis.names.tc.wsrp.v1.types.AccessDeniedFault e6) {
            throw new junit.framework.AssertionFailedError("AccessDenied Exception caught: " + e6);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidHandleFault e7) {
            throw new junit.framework.AssertionFailedError("InvalidHandle Exception caught: " + e7);
        }
            // TBD - validate results
    }

    public void test3WSRPPortletManagementServiceDestroyPortlets() throws Exception {
        oasis.names.tc.wsrp.v1.bind.WSRP_v1_PortletManagement_Binding_SOAPStub binding;
        try {
            binding = (oasis.names.tc.wsrp.v1.bind.WSRP_v1_PortletManagement_Binding_SOAPStub)
                          new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPPortletManagementService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        try {
            oasis.names.tc.wsrp.v1.types.DestroyPortletsResponse value = null;
            value = binding.destroyPortlets(new oasis.names.tc.wsrp.v1.types.DestroyPortlets());
        }
        catch (oasis.names.tc.wsrp.v1.types.InconsistentParametersFault e1) {
            throw new junit.framework.AssertionFailedError("InconsistentParameters Exception caught: " + e1);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidRegistrationFault e2) {
            throw new junit.framework.AssertionFailedError("InvalidRegistration Exception caught: " + e2);
        }
        catch (oasis.names.tc.wsrp.v1.types.OperationFailedFault e3) {
            throw new junit.framework.AssertionFailedError("OperationFailed Exception caught: " + e3);
        }
        catch (oasis.names.tc.wsrp.v1.types.MissingParametersFault e4) {
            throw new junit.framework.AssertionFailedError("MissingParameters Exception caught: " + e4);
        }
            // TBD - validate results
    }

    public void test4WSRPPortletManagementServiceSetPortletProperties() throws Exception {
        oasis.names.tc.wsrp.v1.bind.WSRP_v1_PortletManagement_Binding_SOAPStub binding;
        try {
            binding = (oasis.names.tc.wsrp.v1.bind.WSRP_v1_PortletManagement_Binding_SOAPStub)
                          new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPPortletManagementService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        try {
            oasis.names.tc.wsrp.v1.types.PortletContext value = null;
            value = binding.setPortletProperties(new oasis.names.tc.wsrp.v1.types.SetPortletProperties());
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidUserCategoryFault e1) {
            throw new junit.framework.AssertionFailedError("InvalidUserCategory Exception caught: " + e1);
        }
        catch (oasis.names.tc.wsrp.v1.types.InconsistentParametersFault e2) {
            throw new junit.framework.AssertionFailedError("InconsistentParameters Exception caught: " + e2);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidRegistrationFault e3) {
            throw new junit.framework.AssertionFailedError("InvalidRegistration Exception caught: " + e3);
        }
        catch (oasis.names.tc.wsrp.v1.types.OperationFailedFault e4) {
            throw new junit.framework.AssertionFailedError("OperationFailed Exception caught: " + e4);
        }
        catch (oasis.names.tc.wsrp.v1.types.MissingParametersFault e5) {
            throw new junit.framework.AssertionFailedError("MissingParameters Exception caught: " + e5);
        }
        catch (oasis.names.tc.wsrp.v1.types.AccessDeniedFault e6) {
            throw new junit.framework.AssertionFailedError("AccessDenied Exception caught: " + e6);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidHandleFault e7) {
            throw new junit.framework.AssertionFailedError("InvalidHandle Exception caught: " + e7);
        }
            // TBD - validate results
    }

    public void test5WSRPPortletManagementServiceGetPortletProperties() throws Exception {
        oasis.names.tc.wsrp.v1.bind.WSRP_v1_PortletManagement_Binding_SOAPStub binding;
        try {
            binding = (oasis.names.tc.wsrp.v1.bind.WSRP_v1_PortletManagement_Binding_SOAPStub)
                          new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPPortletManagementService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        try {
            oasis.names.tc.wsrp.v1.types.PropertyList value = null;
            value = binding.getPortletProperties(new oasis.names.tc.wsrp.v1.types.GetPortletProperties());
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidUserCategoryFault e1) {
            throw new junit.framework.AssertionFailedError("InvalidUserCategory Exception caught: " + e1);
        }
        catch (oasis.names.tc.wsrp.v1.types.InconsistentParametersFault e2) {
            throw new junit.framework.AssertionFailedError("InconsistentParameters Exception caught: " + e2);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidRegistrationFault e3) {
            throw new junit.framework.AssertionFailedError("InvalidRegistration Exception caught: " + e3);
        }
        catch (oasis.names.tc.wsrp.v1.types.OperationFailedFault e4) {
            throw new junit.framework.AssertionFailedError("OperationFailed Exception caught: " + e4);
        }
        catch (oasis.names.tc.wsrp.v1.types.MissingParametersFault e5) {
            throw new junit.framework.AssertionFailedError("MissingParameters Exception caught: " + e5);
        }
        catch (oasis.names.tc.wsrp.v1.types.AccessDeniedFault e6) {
            throw new junit.framework.AssertionFailedError("AccessDenied Exception caught: " + e6);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidHandleFault e7) {
            throw new junit.framework.AssertionFailedError("InvalidHandle Exception caught: " + e7);
        }
            // TBD - validate results
    }

    public void test6WSRPPortletManagementServiceGetPortletPropertyDescription() throws Exception {
        oasis.names.tc.wsrp.v1.bind.WSRP_v1_PortletManagement_Binding_SOAPStub binding;
        try {
            binding = (oasis.names.tc.wsrp.v1.bind.WSRP_v1_PortletManagement_Binding_SOAPStub)
                          new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPPortletManagementService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        try {
            oasis.names.tc.wsrp.v1.types.PortletPropertyDescriptionResponse value = null;
            value = binding.getPortletPropertyDescription(new oasis.names.tc.wsrp.v1.types.GetPortletPropertyDescription());
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidUserCategoryFault e1) {
            throw new junit.framework.AssertionFailedError("InvalidUserCategory Exception caught: " + e1);
        }
        catch (oasis.names.tc.wsrp.v1.types.InconsistentParametersFault e2) {
            throw new junit.framework.AssertionFailedError("InconsistentParameters Exception caught: " + e2);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidRegistrationFault e3) {
            throw new junit.framework.AssertionFailedError("InvalidRegistration Exception caught: " + e3);
        }
        catch (oasis.names.tc.wsrp.v1.types.OperationFailedFault e4) {
            throw new junit.framework.AssertionFailedError("OperationFailed Exception caught: " + e4);
        }
        catch (oasis.names.tc.wsrp.v1.types.MissingParametersFault e5) {
            throw new junit.framework.AssertionFailedError("MissingParameters Exception caught: " + e5);
        }
        catch (oasis.names.tc.wsrp.v1.types.AccessDeniedFault e6) {
            throw new junit.framework.AssertionFailedError("AccessDenied Exception caught: " + e6);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidHandleFault e7) {
            throw new junit.framework.AssertionFailedError("InvalidHandle Exception caught: " + e7);
        }
            // TBD - validate results
    }

    public void testWSRPRegistrationServiceWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPRegistrationServiceAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test7WSRPRegistrationServiceRegister() throws Exception {
        oasis.names.tc.wsrp.v1.bind.WSRP_v1_Registration_Binding_SOAPStub binding;
        try {
            binding = (oasis.names.tc.wsrp.v1.bind.WSRP_v1_Registration_Binding_SOAPStub)
                          new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPRegistrationService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        try {
            oasis.names.tc.wsrp.v1.types.RegistrationContext value = null;
            value = binding.register(new oasis.names.tc.wsrp.v1.types.RegistrationData());
        }
        catch (oasis.names.tc.wsrp.v1.types.OperationFailedFault e1) {
            throw new junit.framework.AssertionFailedError("OperationFailed Exception caught: " + e1);
        }
        catch (oasis.names.tc.wsrp.v1.types.MissingParametersFault e2) {
            throw new junit.framework.AssertionFailedError("MissingParameters Exception caught: " + e2);
        }
            // TBD - validate results
    }

    public void test8WSRPRegistrationServiceDeregister() throws Exception {
        oasis.names.tc.wsrp.v1.bind.WSRP_v1_Registration_Binding_SOAPStub binding;
        try {
            binding = (oasis.names.tc.wsrp.v1.bind.WSRP_v1_Registration_Binding_SOAPStub)
                          new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPRegistrationService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        try {
            oasis.names.tc.wsrp.v1.types.ReturnAny value = null;
            value = binding.deregister(new oasis.names.tc.wsrp.v1.types.RegistrationContext());
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidRegistrationFault e1) {
            throw new junit.framework.AssertionFailedError("InvalidRegistration Exception caught: " + e1);
        }
        catch (oasis.names.tc.wsrp.v1.types.OperationFailedFault e2) {
            throw new junit.framework.AssertionFailedError("OperationFailed Exception caught: " + e2);
        }
            // TBD - validate results
    }

    public void test9WSRPRegistrationServiceModifyRegistration() throws Exception {
        oasis.names.tc.wsrp.v1.bind.WSRP_v1_Registration_Binding_SOAPStub binding;
        try {
            binding = (oasis.names.tc.wsrp.v1.bind.WSRP_v1_Registration_Binding_SOAPStub)
                          new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPRegistrationService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        try {
            oasis.names.tc.wsrp.v1.types.RegistrationState value = null;
            value = binding.modifyRegistration(new oasis.names.tc.wsrp.v1.types.ModifyRegistration());
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidRegistrationFault e1) {
            throw new junit.framework.AssertionFailedError("InvalidRegistration Exception caught: " + e1);
        }
        catch (oasis.names.tc.wsrp.v1.types.OperationFailedFault e2) {
            throw new junit.framework.AssertionFailedError("OperationFailed Exception caught: " + e2);
        }
        catch (oasis.names.tc.wsrp.v1.types.MissingParametersFault e3) {
            throw new junit.framework.AssertionFailedError("MissingParameters Exception caught: " + e3);
        }
            // TBD - validate results
    }

    public void testWSRPBaseServiceWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPBaseServiceAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test10WSRPBaseServiceGetMarkup() throws Exception {
        oasis.names.tc.wsrp.v1.bind.WSRP_v1_Markup_Binding_SOAPStub binding;
        try {
            binding = (oasis.names.tc.wsrp.v1.bind.WSRP_v1_Markup_Binding_SOAPStub)
                          new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPBaseService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        try {
            oasis.names.tc.wsrp.v1.types.MarkupResponse value = null;
            value = binding.getMarkup(new oasis.names.tc.wsrp.v1.types.GetMarkup());
        }
        catch (oasis.names.tc.wsrp.v1.types.InconsistentParametersFault e1) {
            throw new junit.framework.AssertionFailedError("InconsistentParameters Exception caught: " + e1);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidRegistrationFault e2) {
            throw new junit.framework.AssertionFailedError("InvalidRegistration Exception caught: " + e2);
        }
        catch (oasis.names.tc.wsrp.v1.types.MissingParametersFault e3) {
            throw new junit.framework.AssertionFailedError("MissingParameters Exception caught: " + e3);
        }
        catch (oasis.names.tc.wsrp.v1.types.OperationFailedFault e4) {
            throw new junit.framework.AssertionFailedError("OperationFailed Exception caught: " + e4);
        }
        catch (oasis.names.tc.wsrp.v1.types.UnsupportedMimeTypeFault e5) {
            throw new junit.framework.AssertionFailedError("UnsupportedMimeType Exception caught: " + e5);
        }
        catch (oasis.names.tc.wsrp.v1.types.UnsupportedModeFault e6) {
            throw new junit.framework.AssertionFailedError("UnsupportedMode Exception caught: " + e6);
        }
        catch (oasis.names.tc.wsrp.v1.types.UnsupportedLocaleFault e7) {
            throw new junit.framework.AssertionFailedError("UnsupportedLocale Exception caught: " + e7);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidUserCategoryFault e8) {
            throw new junit.framework.AssertionFailedError("InvalidUserCategory Exception caught: " + e8);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidSessionFault e9) {
            throw new junit.framework.AssertionFailedError("InvalidSession Exception caught: " + e9);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidCookieFault e10) {
            throw new junit.framework.AssertionFailedError("InvalidCookie Exception caught: " + e10);
        }
        catch (oasis.names.tc.wsrp.v1.types.AccessDeniedFault e11) {
            throw new junit.framework.AssertionFailedError("AccessDenied Exception caught: " + e11);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidHandleFault e12) {
            throw new junit.framework.AssertionFailedError("InvalidHandle Exception caught: " + e12);
        }
        catch (oasis.names.tc.wsrp.v1.types.UnsupportedWindowStateFault e13) {
            throw new junit.framework.AssertionFailedError("UnsupportedWindowState Exception caught: " + e13);
        }
            // TBD - validate results
    }

    public void test11WSRPBaseServicePerformBlockingInteraction() throws Exception {
        oasis.names.tc.wsrp.v1.bind.WSRP_v1_Markup_Binding_SOAPStub binding;
        try {
            binding = (oasis.names.tc.wsrp.v1.bind.WSRP_v1_Markup_Binding_SOAPStub)
                          new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPBaseService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        try {
            oasis.names.tc.wsrp.v1.types.BlockingInteractionResponse value = null;
            value = binding.performBlockingInteraction(new oasis.names.tc.wsrp.v1.types.PerformBlockingInteraction());
        }
        catch (oasis.names.tc.wsrp.v1.types.InconsistentParametersFault e1) {
            throw new junit.framework.AssertionFailedError("InconsistentParameters Exception caught: " + e1);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidRegistrationFault e2) {
            throw new junit.framework.AssertionFailedError("InvalidRegistration Exception caught: " + e2);
        }
        catch (oasis.names.tc.wsrp.v1.types.MissingParametersFault e3) {
            throw new junit.framework.AssertionFailedError("MissingParameters Exception caught: " + e3);
        }
        catch (oasis.names.tc.wsrp.v1.types.OperationFailedFault e4) {
            throw new junit.framework.AssertionFailedError("OperationFailed Exception caught: " + e4);
        }
        catch (oasis.names.tc.wsrp.v1.types.UnsupportedMimeTypeFault e5) {
            throw new junit.framework.AssertionFailedError("UnsupportedMimeType Exception caught: " + e5);
        }
        catch (oasis.names.tc.wsrp.v1.types.UnsupportedModeFault e6) {
            throw new junit.framework.AssertionFailedError("UnsupportedMode Exception caught: " + e6);
        }
        catch (oasis.names.tc.wsrp.v1.types.UnsupportedLocaleFault e7) {
            throw new junit.framework.AssertionFailedError("UnsupportedLocale Exception caught: " + e7);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidUserCategoryFault e8) {
            throw new junit.framework.AssertionFailedError("InvalidUserCategory Exception caught: " + e8);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidSessionFault e9) {
            throw new junit.framework.AssertionFailedError("InvalidSession Exception caught: " + e9);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidCookieFault e10) {
            throw new junit.framework.AssertionFailedError("InvalidCookie Exception caught: " + e10);
        }
        catch (oasis.names.tc.wsrp.v1.types.PortletStateChangeRequiredFault e11) {
            throw new junit.framework.AssertionFailedError("PortletStateChangeRequired Exception caught: " + e11);
        }
        catch (oasis.names.tc.wsrp.v1.types.AccessDeniedFault e12) {
            throw new junit.framework.AssertionFailedError("AccessDenied Exception caught: " + e12);
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidHandleFault e13) {
            throw new junit.framework.AssertionFailedError("InvalidHandle Exception caught: " + e13);
        }
        catch (oasis.names.tc.wsrp.v1.types.UnsupportedWindowStateFault e14) {
            throw new junit.framework.AssertionFailedError("UnsupportedWindowState Exception caught: " + e14);
        }
            // TBD - validate results
    }

    public void test12WSRPBaseServiceReleaseSessions() throws Exception {
        oasis.names.tc.wsrp.v1.bind.WSRP_v1_Markup_Binding_SOAPStub binding;
        try {
            binding = (oasis.names.tc.wsrp.v1.bind.WSRP_v1_Markup_Binding_SOAPStub)
                          new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPBaseService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        try {
            oasis.names.tc.wsrp.v1.types.ReturnAny value = null;
            value = binding.releaseSessions(new oasis.names.tc.wsrp.v1.types.ReleaseSessions());
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidRegistrationFault e1) {
            throw new junit.framework.AssertionFailedError("InvalidRegistration Exception caught: " + e1);
        }
        catch (oasis.names.tc.wsrp.v1.types.OperationFailedFault e2) {
            throw new junit.framework.AssertionFailedError("OperationFailed Exception caught: " + e2);
        }
        catch (oasis.names.tc.wsrp.v1.types.MissingParametersFault e3) {
            throw new junit.framework.AssertionFailedError("MissingParameters Exception caught: " + e3);
        }
        catch (oasis.names.tc.wsrp.v1.types.AccessDeniedFault e4) {
            throw new junit.framework.AssertionFailedError("AccessDenied Exception caught: " + e4);
        }
            // TBD - validate results
    }

    public void test13WSRPBaseServiceInitCookie() throws Exception {
        oasis.names.tc.wsrp.v1.bind.WSRP_v1_Markup_Binding_SOAPStub binding;
        try {
            binding = (oasis.names.tc.wsrp.v1.bind.WSRP_v1_Markup_Binding_SOAPStub)
                          new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPBaseService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        try {
            oasis.names.tc.wsrp.v1.types.ReturnAny value = null;
            value = binding.initCookie(new oasis.names.tc.wsrp.v1.types.InitCookie());
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidRegistrationFault e1) {
            throw new junit.framework.AssertionFailedError("InvalidRegistration Exception caught: " + e1);
        }
        catch (oasis.names.tc.wsrp.v1.types.OperationFailedFault e2) {
            throw new junit.framework.AssertionFailedError("OperationFailed Exception caught: " + e2);
        }
        catch (oasis.names.tc.wsrp.v1.types.AccessDeniedFault e3) {
            throw new junit.framework.AssertionFailedError("AccessDenied Exception caught: " + e3);
        }
            // TBD - validate results
    }

    public void testWSRPServiceDescriptionServiceWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPServiceDescriptionServiceAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test14WSRPServiceDescriptionServiceGetServiceDescription() throws Exception {
        oasis.names.tc.wsrp.v1.bind.WSRP_v1_ServiceDescription_Binding_SOAPStub binding;
        try {
            binding = (oasis.names.tc.wsrp.v1.bind.WSRP_v1_ServiceDescription_Binding_SOAPStub)
                          new oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator().getWSRPServiceDescriptionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        try {
            oasis.names.tc.wsrp.v1.types.ServiceDescription value = null;
            value = binding.getServiceDescription(new oasis.names.tc.wsrp.v1.types.GetServiceDescription());
        }
        catch (oasis.names.tc.wsrp.v1.types.InvalidRegistrationFault e1) {
            throw new junit.framework.AssertionFailedError("InvalidRegistration Exception caught: " + e1);
        }
        catch (oasis.names.tc.wsrp.v1.types.OperationFailedFault e2) {
            throw new junit.framework.AssertionFailedError("OperationFailed Exception caught: " + e2);
        }
            // TBD - validate results
    }

}
