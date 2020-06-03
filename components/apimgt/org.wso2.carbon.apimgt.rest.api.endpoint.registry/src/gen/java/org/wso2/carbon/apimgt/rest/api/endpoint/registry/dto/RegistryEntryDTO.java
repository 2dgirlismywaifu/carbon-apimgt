package org.wso2.carbon.apimgt.rest.api.endpoint.registry.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import javax.validation.constraints.*;

    import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

import javax.xml.bind.annotation.*;



public class RegistryEntryDTO   {
    private String id = null;
    private String entryName = null;
    private String displayName = null;
    private String description = null;
    private String version = null;
    private String serviceUrl = null;
            @XmlType(name="ServiceCategoryEnum")
            @XmlEnum(String.class)
            public enum ServiceCategoryEnum {
            
                @XmlEnumValue("UTILITY") UTILITY(String.valueOf("UTILITY")), @XmlEnumValue("EDGE") EDGE(String.valueOf("EDGE")), @XmlEnumValue("DOMAIN") DOMAIN(String.valueOf("DOMAIN"));
            
            
            private String value;
            
            ServiceCategoryEnum (String v) {
            value = v;
            }
            
            public String value() {
            return value;
            }
            
            @Override
            public String toString() {
            return String.valueOf(value);
            }
            
            public static ServiceCategoryEnum fromValue(String v) {
            for (ServiceCategoryEnum b : ServiceCategoryEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
            return b;
            }
            }
            return null;
            }
            }    private ServiceCategoryEnum serviceCategory = null;
            @XmlType(name="ServiceTypeEnum")
            @XmlEnum(String.class)
            public enum ServiceTypeEnum {
            
                @XmlEnumValue("REST") REST(String.valueOf("REST")), @XmlEnumValue("SOAP_1_1") SOAP_1_1(String.valueOf("SOAP_1_1")), @XmlEnumValue("GQL") GQL(String.valueOf("GQL")), @XmlEnumValue("WS") WS(String.valueOf("WS"));
            
            
            private String value;
            
            ServiceTypeEnum (String v) {
            value = v;
            }
            
            public String value() {
            return value;
            }
            
            @Override
            public String toString() {
            return String.valueOf(value);
            }
            
            public static ServiceTypeEnum fromValue(String v) {
            for (ServiceTypeEnum b : ServiceTypeEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
            return b;
            }
            }
            return null;
            }
            }    private ServiceTypeEnum serviceType = null;
            @XmlType(name="DefinitionTypeEnum")
            @XmlEnum(String.class)
            public enum DefinitionTypeEnum {
            
                @XmlEnumValue("OAS") OAS(String.valueOf("OAS")), @XmlEnumValue("WSDL1") WSDL1(String.valueOf("WSDL1")), @XmlEnumValue("WSDL2") WSDL2(String.valueOf("WSDL2")), @XmlEnumValue("GQL_SDL") GQL_SDL(String.valueOf("GQL_SDL"));
            
            
            private String value;
            
            DefinitionTypeEnum (String v) {
            value = v;
            }
            
            public String value() {
            return value;
            }
            
            @Override
            public String toString() {
            return String.valueOf(value);
            }
            
            public static DefinitionTypeEnum fromValue(String v) {
            for (DefinitionTypeEnum b : DefinitionTypeEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
            return b;
            }
            }
            return null;
            }
            }    private DefinitionTypeEnum definitionType = null;
    private String definitionUrl = null;

    /**
    **/
    public RegistryEntryDTO id(String id) {
    this.id = id;
    return this;
    }

    
    
    @Schema(example = "01234567-0123-0123-0123-012345678901", description = "")
    @JsonProperty("id")
          public String getId() {
    return id;
    }
    public void setId(String id) {
    this.id = id;
    }

    /**
    **/
    public RegistryEntryDTO entryName(String entryName) {
    this.entryName = entryName;
    return this;
    }

    
    
    @Schema(example = "Pizzashack-Endpoint", required = true, description = "")
    @JsonProperty("entryName")
            @NotNull
     @Pattern(regexp="^[^\\*]+$")  public String getEntryName() {
    return entryName;
    }
    public void setEntryName(String entryName) {
    this.entryName = entryName;
    }

    /**
    **/
    public RegistryEntryDTO displayName(String displayName) {
    this.displayName = displayName;
    return this;
    }



    @Schema(example = "Pizzashack-Endpoint", description = "")
    @JsonProperty("displayName")
         @Pattern(regexp="^[^\\*]+$")  public String getDisplayName() {
    return displayName;
    }
    public void setDisplayName(String displayName) {
    this.displayName = displayName;
    }

    /**
    **/
    public RegistryEntryDTO description(String description) {
    this.description = description;
    return this;
    }

    
    
    @Schema(example = "A Registry Entry that exposes a REST endpoint", description = "")
    @JsonProperty("description")
          public String getDescription() {
    return description;
    }
    public void setDescription(String description) {
    this.description = description;
    }

    /**
    **/
    public RegistryEntryDTO version(String version) {
    this.version = version;
    return this;
    }

    
    
    @Schema(example = "v1", required = true, description = "")
    @JsonProperty("version")
            @NotNull
      public String getVersion() {
    return version;
    }
    public void setVersion(String version) {
    this.version = version;
    }

    /**
    **/
    public RegistryEntryDTO serviceUrl(String serviceUrl) {
    this.serviceUrl = serviceUrl;
    return this;
    }

    
    
    @Schema(example = "http://localhost/pizzashack", description = "")
    @JsonProperty("serviceUrl")
          public String getServiceUrl() {
    return serviceUrl;
    }
    public void setServiceUrl(String serviceUrl) {
    this.serviceUrl = serviceUrl;
    }

    /**
        * Business Category of the Endpoint
    **/
    public RegistryEntryDTO serviceCategory(ServiceCategoryEnum serviceCategory) {
    this.serviceCategory = serviceCategory;
    return this;
    }

    
    
    @Schema(description = "Business Category of the Endpoint")
    @JsonProperty("serviceCategory")
          public ServiceCategoryEnum getServiceCategory() {
    return serviceCategory;
    }
    public void setServiceCategory(ServiceCategoryEnum serviceCategory) {
    this.serviceCategory = serviceCategory;
    }

    /**
        * Type of the backend connecting to
    **/
    public RegistryEntryDTO serviceType(ServiceTypeEnum serviceType) {
    this.serviceType = serviceType;
    return this;
    }

    
    
    @Schema(example = "REST", description = "Type of the backend connecting to")
    @JsonProperty("serviceType")
          public ServiceTypeEnum getServiceType() {
    return serviceType;
    }
    public void setServiceType(ServiceTypeEnum serviceType) {
    this.serviceType = serviceType;
    }

    /**
        * The type of the provided API definition
    **/
    public RegistryEntryDTO definitionType(DefinitionTypeEnum definitionType) {
    this.definitionType = definitionType;
    return this;
    }

    
    
    @Schema(example = "OAS", description = "The type of the provided API definition")
    @JsonProperty("definitionType")
          public DefinitionTypeEnum getDefinitionType() {
    return definitionType;
    }
    public void setDefinitionType(DefinitionTypeEnum definitionType) {
    this.definitionType = definitionType;
    }

    /**
    **/
    public RegistryEntryDTO definitionUrl(String definitionUrl) {
    this.definitionUrl = definitionUrl;
    return this;
    }

    
    
    @Schema(example = "http://localhost/pizzashack?swagger.json", description = "")
    @JsonProperty("definitionUrl")
          public String getDefinitionUrl() {
    return definitionUrl;
    }
    public void setDefinitionUrl(String definitionUrl) {
    this.definitionUrl = definitionUrl;
    }


@Override
public boolean equals(java.lang.Object o) {
if (this == o) {
return true;
}
if (o == null || getClass() != o.getClass()) {
return false;
}
RegistryEntryDTO registryEntry = (RegistryEntryDTO) o;
    return Objects.equals(id, registryEntry.id) &&
    Objects.equals(entryName, registryEntry.entryName) &&
    Objects.equals(displayName, registryEntry.displayName) &&
    Objects.equals(description, registryEntry.description) &&
    Objects.equals(version, registryEntry.version) &&
    Objects.equals(serviceUrl, registryEntry.serviceUrl) &&
    Objects.equals(serviceCategory, registryEntry.serviceCategory) &&
    Objects.equals(serviceType, registryEntry.serviceType) &&
    Objects.equals(definitionType, registryEntry.definitionType) &&
    Objects.equals(definitionUrl, registryEntry.definitionUrl);
}

@Override
public int hashCode() {
return Objects.hash(id, entryName, displayName, description, version, serviceUrl, serviceCategory, serviceType, definitionType, definitionUrl);
}

@Override
public String toString() {
StringBuilder sb = new StringBuilder();
sb.append("class RegistryEntryDTO {\n");

sb.append("    id: ").append(toIndentedString(id)).append("\n");
sb.append("    entryName: ").append(toIndentedString(entryName)).append("\n");
sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
sb.append("    description: ").append(toIndentedString(description)).append("\n");
sb.append("    version: ").append(toIndentedString(version)).append("\n");
sb.append("    serviceUrl: ").append(toIndentedString(serviceUrl)).append("\n");
sb.append("    serviceCategory: ").append(toIndentedString(serviceCategory)).append("\n");
sb.append("    serviceType: ").append(toIndentedString(serviceType)).append("\n");
sb.append("    definitionType: ").append(toIndentedString(definitionType)).append("\n");
sb.append("    definitionUrl: ").append(toIndentedString(definitionUrl)).append("\n");
sb.append("}");
return sb.toString();
}

/**
* Convert the given object to string with each line indented by 4 spaces
* (except the first line).
*/
private String toIndentedString(java.lang.Object o) {
if (o == null) {
return "null";
}
return o.toString().replace("\n", "\n    ");
}
}