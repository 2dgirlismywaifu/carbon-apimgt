/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.apimgt.impl.endpoint.registry.dao;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.endpoint.registry.model.EndpointRegistryEntryFilterParams;
import org.wso2.carbon.apimgt.impl.endpoint.registry.constants.EndpointRegistryConstants;
import org.wso2.carbon.apimgt.impl.endpoint.registry.constants.SQLConstants;
import org.wso2.carbon.apimgt.api.endpoint.registry.model.EndpointRegistryEntry;
import org.wso2.carbon.apimgt.api.endpoint.registry.api.EndpointRegistryException;
import org.wso2.carbon.apimgt.api.endpoint.registry.model.EndpointRegistryInfo;
import org.wso2.carbon.apimgt.impl.factory.SQLConstantManagerFactory;
import org.wso2.carbon.apimgt.impl.utils.APIMgtDBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class represent the EndpointRegistryDAO.
 */
public class EndpointRegistryDAO {

    private static final Log log = LogFactory.getLog(EndpointRegistryDAO.class);
    private static EndpointRegistryDAO INSTANCE = null;

    /**
     * Method to get the instance of the EndpointRegistryDAO.
     *
     * @return {@link EndpointRegistryDAO} instance
     */
    public static EndpointRegistryDAO getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new EndpointRegistryDAO();
        }

        return INSTANCE;
    }

    private void handleException(String msg, Throwable t) throws EndpointRegistryException {

        log.error(msg, t);
        throw new EndpointRegistryException(msg, t);
    }

    /**
     * Add a new endpoint registry
     *
     * @param endpointRegistry EndpointRegistryInfo
     * @param tenantID         ID of the owner's tenant
     * @return registryId
     */
    public String addEndpointRegistry(EndpointRegistryInfo endpointRegistry, int tenantID)
            throws EndpointRegistryException {

        String query = SQLConstants.ADD_ENDPOINT_REGISTRY_SQL;
        String uuid = UUID.randomUUID().toString();
        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);
            ps.setString(1, uuid);
            ps.setString(2, endpointRegistry.getName());
            ps.setString(3, endpointRegistry.getDisplayName());
            ps.setString(4, endpointRegistry.getType());
            ps.setInt(5, tenantID);
            ps.setString(6, endpointRegistry.getOwner());
            ps.setString(7, endpointRegistry.getOwner());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(8, timestamp);
            ps.setTimestamp(9, timestamp);

            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            handleException("Error while adding new endpoint registry: " + endpointRegistry.getName(), e);
        }
        return uuid;
    }

    /**
     * Update an existing endpoint registry.
     *
     * @param registryId       uuid of the endpoint registry
     * @param endpointRegistry EndpointRegistryInfo object with updated details
     * @param username         logged in username
     * @throws EndpointRegistryException if unable to update the endpoint registry
     */
    public void updateEndpointRegistry(String registryId, EndpointRegistryInfo endpointRegistry, String username)
            throws EndpointRegistryException {

        String query = SQLConstants.UPDATE_ENDPOINT_REGISTRY_SQL;
        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);
            ps.setString(1, endpointRegistry.getDisplayName());
            ps.setString(2, endpointRegistry.getType());
            ps.setString(3, username);
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            ps.setString(5, registryId);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            handleException("Error while updating endpoint registry: " + endpointRegistry.getName(), e);
        }
        return;
    }

    /**
     * Return the details of an Endpoint Registry
     *
     * @param registryId Endpoint Registry Identifier
     * @param tenantID   ID of the owner's tenant
     * @return Endpoint Registry Object
     * @throws EndpointRegistryException
     */
    public EndpointRegistryInfo getEndpointRegistryByUUID(String registryId, int tenantID)
            throws EndpointRegistryException {

        String query = SQLConstants.GET_ENDPOINT_REGISTRY_BY_UUID;
        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, registryId);
            ps.setInt(2, tenantID);
            ps.executeQuery();
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EndpointRegistryInfo endpointRegistry = new EndpointRegistryInfo();
                    endpointRegistry.setUuid(rs.getString(EndpointRegistryConstants.COLUMN_UUID));
                    endpointRegistry.setName(rs.getString(EndpointRegistryConstants.COLUMN_REG_NAME));
                    endpointRegistry.setDisplayName(rs.getString(EndpointRegistryConstants.COLUMN_DISPLAY_NAME));
                    endpointRegistry.setType(rs.getString(EndpointRegistryConstants.COLUMN_REG_TYPE));
                    endpointRegistry.setRegistryId(rs.getInt(EndpointRegistryConstants.COLUMN_ID));
                    endpointRegistry.setOwner(rs.getString(EndpointRegistryConstants.COLUMN_CREATED_BY));
                    endpointRegistry.setUpdatedBy(rs.getString(EndpointRegistryConstants.COLUMN_UPDATED_BY));

                    Timestamp createdTime = rs.getTimestamp(EndpointRegistryConstants.COLUMN_CREATED_TIME);
                    endpointRegistry.setCreatedTime(
                            createdTime == null ? null : String.valueOf(createdTime.getTime()));

                    Timestamp updatedTime = rs.getTimestamp(EndpointRegistryConstants.COLUMN_UPDATED_TIME);
                    endpointRegistry.setLastUpdatedTime(
                            updatedTime == null ? null : String.valueOf(updatedTime.getTime()));

                    return endpointRegistry;
                }
            }
        } catch (SQLException e) {
            handleException("Error while retrieving details of endpoint registry with Id: "
                    + registryId, e);
        }
        return null;
    }

    /**
     * Deletes an Endpoint Registry
     *
     * @param registryUUID Registry Identifier(UUID)
     * @throws EndpointRegistryException if failed to delete the Endpoint Registry
     */
    public void deleteEndpointRegistry(String registryUUID) throws EndpointRegistryException {

        String deleteRegQuery = SQLConstants.DELETE_ENDPOINT_REGISTRY_SQL;

        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement statementDeleteRegistry = connection.prepareStatement(deleteRegQuery)
        ) {
            connection.setAutoCommit(false);
            statementDeleteRegistry.setString(1, registryUUID);
            statementDeleteRegistry.execute();
            connection.commit();
        } catch (SQLException e) {
            handleException("Failed to delete Endpoint Registry with the id: " + registryUUID, e);
        }
    }

    /**
     * Checks whether the given endpoint registry name is already available under given tenant domain
     *
     * @param registryName  Registry name
     * @param isDisplayName Whether the display name or not
     * @param tenantID      Tenant Identifier
     * @return boolean
     * @throws EndpointRegistryException
     */
    public boolean isEndpointRegistryNameExists(String registryName, boolean isDisplayName, int tenantID)
            throws EndpointRegistryException {

        String sql;
        if (isDisplayName) {
            sql = SQLConstants.IS_ENDPOINT_REGISTRY_DISPLAY_NAME_EXISTS;
        } else {
            sql = SQLConstants.IS_ENDPOINT_REGISTRY_NAME_EXISTS;
        }
        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, registryName);
            statement.setInt(2, tenantID);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("ENDPOINT_REGISTRY_COUNT");
                if (count > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            handleException("Failed to check the existence of Endpoint Registry: " + registryName, e);
        }
        return false;
    }

    /**
     * Checks whether an endpoint registry of given type is already available under given tenant domain
     *
     * @param registryType Registry type
     * @param tenantID     Tenant Identifier
     * @return boolean
     * @throws EndpointRegistryException
     */
    public boolean isEndpointRegistryTypeExists(String registryType, int tenantID) throws EndpointRegistryException {

        String sql = SQLConstants.IS_ENDPOINT_REGISTRY_TYPE_EXISTS;
        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, registryType);
            statement.setInt(2, tenantID);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("ENDPOINT_REGISTRY_COUNT");
                if (count > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            handleException("Failed to check the existence of Endpoint Registry with type: " + registryType, e);
        }
        return false;
    }

    /**
     * Return the details of the Endpoint Registry of the given tenant OF WSO2 type
     *
     * @param tenantID
     * @return A EndpointRegistryInfo object
     * @throws EndpointRegistryException if failed to get details of Endpoint Registry
     */
    public EndpointRegistryInfo getEndpointRegistry(int tenantID) throws EndpointRegistryException {

        String query = SQLConstants.GET_ENDPOINT_REGISTRY_OF_TENANT_WITH_TYPE;

        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            // Currently retrieve the registry of WSO2 type only
            ps.setString(1, EndpointRegistryConstants.REGISTRY_TYPE_WSO2);
            ps.setInt(2, tenantID);

            ps.executeQuery();
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EndpointRegistryInfo endpointRegistry = new EndpointRegistryInfo();
                    endpointRegistry.setUuid(rs.getString(EndpointRegistryConstants.COLUMN_UUID));
                    endpointRegistry.setName(rs.getString(EndpointRegistryConstants.COLUMN_REG_NAME));
                    endpointRegistry.setDisplayName(rs.getString(EndpointRegistryConstants.COLUMN_DISPLAY_NAME));
                    endpointRegistry.setType(rs.getString(EndpointRegistryConstants.COLUMN_REG_TYPE));
                    endpointRegistry.setOwner(rs.getString(EndpointRegistryConstants.COLUMN_CREATED_BY));
                    endpointRegistry.setUpdatedBy(rs.getString(EndpointRegistryConstants.COLUMN_UPDATED_BY));

                    Timestamp createdTime = rs.getTimestamp(EndpointRegistryConstants.COLUMN_CREATED_TIME);
                    endpointRegistry.setCreatedTime(
                            createdTime == null ? null : String.valueOf(createdTime.getTime()));

                    Timestamp updatedTime = rs.getTimestamp(EndpointRegistryConstants.COLUMN_UPDATED_TIME);
                    endpointRegistry.setLastUpdatedTime(
                            updatedTime == null ? null : String.valueOf(updatedTime.getTime()));

                    return endpointRegistry;
                }
            }
        } catch (SQLException e) {
            handleException("Error while retrieving details of endpoint registries", e);
        }
        return null;
    }

    /**
     * Returns the details of an endpoint registry entry.
     *
     * @param registryEntryUuid endpoint registry entry identifier.
     * @return EndpointRegistryEntry object.
     * @throws EndpointRegistryException
     */
    public EndpointRegistryEntry getEndpointRegistryEntryByUUID(String registryEntryUuid)
            throws EndpointRegistryException {

        String query = SQLConstants.GET_ENDPOINT_REGISTRY_ENTRY_BY_UUID;
        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, registryEntryUuid);
            ps.executeQuery();
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EndpointRegistryEntry endpointRegistryEntry = new EndpointRegistryEntry();
                    endpointRegistryEntry.setEntryId(rs.getString(EndpointRegistryConstants.COLUMN_UUID));
                    endpointRegistryEntry.setEntryName(rs.getString(EndpointRegistryConstants.COLUMN_ENTRY_NAME));
                    endpointRegistryEntry.setDisplayName(rs.getString(EndpointRegistryConstants.COLUMN_DISPLAY_NAME));
                    endpointRegistryEntry.setVersion(rs.getString(EndpointRegistryConstants.COLUMN_ENTRY_VERSION));
                    endpointRegistryEntry.setDescription(rs.getString(EndpointRegistryConstants.COLUMN_DESCRIPTION));
                    endpointRegistryEntry.setDefinitionType(
                            rs.getString(EndpointRegistryConstants.COLUMN_DEFINITION_TYPE));
                    endpointRegistryEntry.setDefinitionURL(
                            rs.getString(EndpointRegistryConstants.COLUMN_DEFINITION_URL));
                    endpointRegistryEntry.setServiceType(rs.getString(EndpointRegistryConstants.COLUMN_SERVICE_TYPE));
                    endpointRegistryEntry.setServiceCategory(rs.getString(EndpointRegistryConstants.
                            COLUMN_SERVICE_CATEGORY));
                    endpointRegistryEntry.setServiceURL(rs.getString(EndpointRegistryConstants.COLUMN_SERVICE_URL));
                    endpointRegistryEntry.setEndpointDefinition(
                            rs.getBinaryStream(EndpointRegistryConstants.COLUMN_ENDPOINT_DEFINITION));
                    endpointRegistryEntry.setOwner(rs.getString(EndpointRegistryConstants.COLUMN_CREATED_BY));
                    endpointRegistryEntry.setUpdatedBy(rs.getString(EndpointRegistryConstants.COLUMN_UPDATED_BY));

                    Timestamp createdTime = rs.getTimestamp(EndpointRegistryConstants.COLUMN_CREATED_TIME);
                    endpointRegistryEntry.setCreatedTime(
                            createdTime == null ? null : String.valueOf(createdTime.getTime()));

                    Timestamp updatedTime = rs.getTimestamp(EndpointRegistryConstants.COLUMN_UPDATED_TIME);
                    endpointRegistryEntry.setLastUpdatedTime(
                            updatedTime == null ? null : String.valueOf(updatedTime.getTime()));

                    endpointRegistryEntry.setRegistryId(rs.getInt(EndpointRegistryConstants.COLUMN_REG_ID));
                    return endpointRegistryEntry;
                }
            }
        } catch (SQLException e) {
            handleException("Error while retrieving details of endpoint registry with Id: "
                    + registryEntryUuid, e);
        }
        return null;

    }

    /**
     * {@inheritDoc}
     */
    public List<EndpointRegistryEntry> getEndpointRegistryEntries(EndpointRegistryEntryFilterParams filterParams,
                                                                  String registryId) throws EndpointRegistryException {

        List<EndpointRegistryEntry> endpointRegistryEntryList = new ArrayList<>();
        String query;
        boolean versionMatch = StringUtils.isNotEmpty(filterParams.getVersion());
        boolean entryNameMatch = StringUtils.isNotEmpty(filterParams.getEntryName());
        try {
            if (versionMatch && entryNameMatch) {
                query = SQLConstantManagerFactory
                        .getSQlString("GET_ALL_ENTRIES_OF_ENDPOINT_REGISTRY_WITH_VERSION_AND_NAME");
            } else if (versionMatch) {
                query = SQLConstantManagerFactory.getSQlString("GET_ALL_ENTRIES_OF_ENDPOINT_REGISTRY_WITH_VERSION");
            } else if (entryNameMatch) {
                query = SQLConstantManagerFactory.getSQlString("GET_ALL_ENTRIES_OF_ENDPOINT_REGISTRY_WITH_NAME");
            } else {
                query = SQLConstantManagerFactory.getSQlString("GET_ALL_ENTRIES_OF_ENDPOINT_REGISTRY");
            }
            query = query.replace("$1", filterParams.getSortBy());
            query = query.replace("$2", filterParams.getSortOrder());

            try (Connection connection = APIMgtDBUtil.getConnection();
                 PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, registryId);
                String displayName = filterParams.getDisplayName();
                if (StringUtils.isNotEmpty(filterParams.getDisplayName())) {
                    displayName = displayName.replaceAll("\\*", "%");
                } else {
                    displayName = "%" + displayName + "%";
                }
                ps.setString(2, displayName);
                ps.setString(3, "%" + filterParams.getDefinitionType() + "%");
                ps.setString(4, "%" + filterParams.getServiceType() + "%");
                ps.setString(5, "%" + filterParams.getServiceCategory() + "%");
                if (versionMatch && entryNameMatch) {
                    ps.setString(6, filterParams.getVersion());
                    ps.setString(7, filterParams.getEntryName());
                    ps.setInt(8, filterParams.getOffset());
                    ps.setInt(9, filterParams.getLimit());
                } else if (versionMatch) {
                    ps.setString(6, filterParams.getVersion());
                    ps.setInt(7, filterParams.getOffset());
                    ps.setInt(8, filterParams.getLimit());
                }  else if (entryNameMatch) {
                    ps.setString(6, filterParams.getEntryName());
                    ps.setInt(7, filterParams.getOffset());
                    ps.setInt(8, filterParams.getLimit());
                } else {
                    ps.setInt(6, filterParams.getOffset());
                    ps.setInt(7, filterParams.getLimit());
                }
                ps.executeQuery();
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        EndpointRegistryEntry endpointRegistryEntry = new EndpointRegistryEntry();
                        endpointRegistryEntry.setEntryId(rs.getString(EndpointRegistryConstants.COLUMN_UUID));
                        endpointRegistryEntry.setEntryName(rs.getString(EndpointRegistryConstants.COLUMN_ENTRY_NAME));
                        endpointRegistryEntry.setDisplayName(
                                rs.getString(EndpointRegistryConstants.COLUMN_DISPLAY_NAME));
                        endpointRegistryEntry.setVersion(rs.getString(EndpointRegistryConstants.COLUMN_ENTRY_VERSION));
                        endpointRegistryEntry.setDescription(rs.getString(EndpointRegistryConstants.
                                COLUMN_DESCRIPTION));
                        endpointRegistryEntry.setServiceURL(rs.getString(EndpointRegistryConstants.COLUMN_SERVICE_URL));
                        endpointRegistryEntry.setDefinitionType(rs.getString(EndpointRegistryConstants.
                                COLUMN_DEFINITION_TYPE));
                        endpointRegistryEntry.setDefinitionURL(rs.getString(EndpointRegistryConstants.
                                COLUMN_DEFINITION_URL));
                        endpointRegistryEntry.setServiceType(rs.getString(EndpointRegistryConstants.COLUMN_SERVICE_TYPE));
                        endpointRegistryEntry.setServiceCategory(rs.getString(EndpointRegistryConstants
                                .COLUMN_SERVICE_CATEGORY));
                        endpointRegistryEntry.setOwner(rs.getString(EndpointRegistryConstants.COLUMN_CREATED_BY));
                        endpointRegistryEntry.setUpdatedBy(rs.getString(EndpointRegistryConstants.COLUMN_UPDATED_BY));

                        Timestamp createdTime = rs.getTimestamp(EndpointRegistryConstants.COLUMN_CREATED_TIME);
                        endpointRegistryEntry.setCreatedTime(
                                createdTime == null ? null : String.valueOf(createdTime.getTime()));

                        Timestamp updatedTime = rs.getTimestamp(EndpointRegistryConstants.COLUMN_UPDATED_TIME);
                        endpointRegistryEntry.setLastUpdatedTime(
                                updatedTime == null ? null : String.valueOf(updatedTime.getTime()));
                        endpointRegistryEntryList.add(endpointRegistryEntry);
                    }
                }
            } catch (SQLException e) {
                handleException("Error while retrieving entries of endpoint registry", e);
            }
        } catch (APIManagementException e) {
            handleException("Error while retrieving the SQL string", e);
        }
        return endpointRegistryEntryList;
    }

    /**
     * Add a new endpoint registry entry
     *
     * @param registryEntry EndpointRegistryEntry
     * @param username      logged in username
     * @return registryId
     */
    public String addEndpointRegistryEntry(EndpointRegistryEntry registryEntry, String username)
            throws EndpointRegistryException {

        String query = SQLConstants.ADD_ENDPOINT_REGISTRY_ENTRY_SQL;
        String uuid = UUID.randomUUID().toString();
        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);
            ps.setString(1, uuid);
            ps.setString(2, registryEntry.getEntryName());
            ps.setString(3, registryEntry.getDisplayName());
            ps.setString(4, registryEntry.getVersion());
            ps.setString(5, registryEntry.getServiceURL());
            ps.setString(6, registryEntry.getDefinitionType());
            ps.setString(7, registryEntry.getDefinitionURL());
            ps.setString(8, registryEntry.getDescription());
            ps.setString(9, registryEntry.getServiceType());
            ps.setString(10, registryEntry.getServiceCategory());
            ps.setBlob(11, registryEntry.getEndpointDefinition());
            ps.setInt(12, registryEntry.getRegistryId());
            ps.setString(13, username);
            ps.setString(14, username);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(15, timestamp);
            ps.setTimestamp(16, timestamp);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            handleException("Error while adding new endpoint registry entry: " + registryEntry.getEntryName(), e);
        }
        return uuid;
    }

    /**
     * Updates Registry Entry
     *
     * @param registryEntry EndpointRegistryEntry
     * @param username      logged in username
     * @throws EndpointRegistryException if failed to update EndpointRegistryEntry
     */
    public void updateEndpointRegistryEntry(EndpointRegistryEntry registryEntry, String username)
            throws EndpointRegistryException {

        String query = SQLConstants.UPDATE_ENDPOINT_REGISTRY_ENTRY_SQL;
        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);
            ps.setString(1, registryEntry.getDisplayName());
            ps.setString(2, registryEntry.getVersion());
            ps.setString(3, registryEntry.getServiceURL());
            ps.setString(4, registryEntry.getDefinitionType());
            ps.setString(5, registryEntry.getDefinitionURL());
            ps.setString(6, registryEntry.getDescription());
            ps.setString(7, registryEntry.getServiceType());
            ps.setString(8, registryEntry.getServiceCategory());
            ps.setBlob(9, registryEntry.getEndpointDefinition());
            ps.setString(10, username);
            ps.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
            ps.setString(12, registryEntry.getEntryId());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            handleException("Error while updating endpoint registry entry with id: " + registryEntry.getEntryId(), e);
        }
    }

    /**
     * Deletes an Endpoint Registry Entry
     *
     * @param entryId Registry Entry Identifier(UUID)
     * @throws EndpointRegistryException if failed to delete the Endpoint Registry Entry
     */
    public void deleteEndpointRegistryEntry(String entryId) throws EndpointRegistryException {

        String query = SQLConstants.DELETE_ENDPOINT_REGISTRY_ENTRY_SQL;

        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)
        ) {
            connection.setAutoCommit(false);
            statement.setString(1, entryId);
            statement.execute();
            connection.commit();
        } catch (SQLException e) {
            handleException("Failed to delete Endpoint Registry Entry with the id: " + entryId, e);
        }
    }

    /**
     * Checks whether the given endpoint registry entry name is already available under given registry
     *
     * @param registryEntry Endpoint Registry Entry
     * @param isDisplayName Whether display name or not
     * @return boolean
     */
    public boolean isRegistryEntryNameExists(EndpointRegistryEntry registryEntry, boolean isDisplayName)
            throws EndpointRegistryException {

        String sql;
        if (isDisplayName) {
            sql = SQLConstants.IS_ENDPOINT_REGISTRY_ENTRY_DISPLAY_NAME_EXISTS;
        } else {
            sql = SQLConstants.IS_ENDPOINT_REGISTRY_ENTRY_NAME_EXISTS;
        }
        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (isDisplayName) {
                statement.setString(1, registryEntry.getDisplayName());
            } else {
                statement.setString(1, registryEntry.getEntryName());
            }
            statement.setInt(2, registryEntry.getRegistryId());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("REGISTRY_ENTRY_COUNT");
                if (count > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            handleException("Failed to check the existence of Registry Entry: " + registryEntry.getEntryName(), e);
        }
        return false;
    }

    /**
     * Checks whether the given endpoint registry entry name and version is already available under given registry
     *
     * @param registryEntry EndpointRegistryEntry
     * @return boolean
     */
    public boolean isRegistryEntryNameAndVersionExists(EndpointRegistryEntry registryEntry)
            throws EndpointRegistryException {

        String sql = SQLConstants.IS_ENDPOINT_REGISTRY_ENTRY_NAME_AND_VERSION_EXISTS;
        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, registryEntry.getEntryName());
            statement.setString(2, registryEntry.getVersion());
            statement.setInt(3, registryEntry.getRegistryId());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("REGISTRY_ENTRY_COUNT");
                if (count > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            handleException("Failed to check the existence of Registry Entry: " + registryEntry.getEntryName(), e);
        }
        return false;
    }
}
