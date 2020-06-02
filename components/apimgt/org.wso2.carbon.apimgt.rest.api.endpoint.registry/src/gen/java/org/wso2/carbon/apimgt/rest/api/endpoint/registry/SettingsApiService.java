package org.wso2.carbon.apimgt.rest.api.endpoint.registry;

import org.wso2.carbon.apimgt.rest.api.endpoint.registry.*;
import org.wso2.carbon.apimgt.rest.api.endpoint.registry.dto.*;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import org.wso2.carbon.apimgt.api.endpoint.registry.api.EndpointRegistryException;


import java.util.List;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.wso2.carbon.apimgt.rest.api.endpoint.registry.RegistriesApi.*;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJAXRSCXFCDIServerCodegen", date = "2020-06-02T16:31:35.177+05:30[Asia/Colombo]")public interface SettingsApiService {
        public Response getSettings(MessageContext messageContext) throws EndpointRegistryException;
}
