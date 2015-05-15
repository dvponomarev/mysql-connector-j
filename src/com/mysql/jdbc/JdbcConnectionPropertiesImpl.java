/*
  Copyright (c) 2002, 2015, Oracle and/or its affiliates. All rights reserved.

  The MySQL Connector/J is licensed under the terms of the GPLv2
  <http://www.gnu.org/licenses/old-licenses/gpl-2.0.html>, like most MySQL Connectors.
  There are special exceptions to the terms and conditions of the GPLv2 as it is applied to
  this software, see the FLOSS License Exception
  <http://www.mysql.com/about/legal/licensing/foss-exception.html>.

  This program is free software; you can redistribute it and/or modify it under the terms
  of the GNU General Public License as published by the Free Software Foundation; version 2
  of the License.

  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
  See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with this
  program; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth
  Floor, Boston, MA 02110-1301  USA

 */

package com.mysql.jdbc;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Reference;

import com.mysql.cj.api.conf.IntegerReadonlyProperty;
import com.mysql.cj.api.conf.LongReadonlyProperty;
import com.mysql.cj.api.conf.PropertyDefinition;
import com.mysql.cj.core.Messages;
import com.mysql.cj.core.conf.BooleanConnectionProperty;
import com.mysql.cj.core.conf.CommonConnectionProperties;
import com.mysql.cj.core.conf.ConnectionProperty;
import com.mysql.cj.core.conf.IntegerConnectionProperty;
import com.mysql.cj.core.conf.LongConnectionProperty;
import com.mysql.cj.core.conf.MemorySizeConnectionProperty;
import com.mysql.cj.core.conf.PropertyDefinitions;
import com.mysql.cj.core.conf.StringConnectionProperty;
import com.mysql.cj.core.exception.CJException;
import com.mysql.cj.core.exception.ExceptionFactory;
import com.mysql.cj.core.exception.WrongArgumentException;
import com.mysql.cj.core.util.StringUtils;
import com.mysql.jdbc.exceptions.SQLError;

/**
 * Represents configurable properties for Connections and DataSources. Can also expose properties as JDBC DriverPropertyInfo if required as well.
 */
public class JdbcConnectionPropertiesImpl extends CommonConnectionProperties implements Serializable, JdbcConnectionProperties {

    private static final long serialVersionUID = -1550312215415685578L;

    /**
     * Exposes all ConnectionPropertyInfo instances as DriverPropertyInfo
     * 
     * @param info
     *            the properties to load into these ConnectionPropertyInfo
     *            instances
     * @param slotsToReserve
     *            the number of DPI slots to reserve for 'standard' DPI
     *            properties (user, host, password, etc)
     * @return a list of all ConnectionPropertyInfo instances, as
     *         DriverPropertyInfo
     * @throws SQLException
     *             if an error occurs
     */
    protected static DriverPropertyInfo[] exposeAsDriverPropertyInfo(Properties info, int slotsToReserve) {
        return (new JdbcConnectionPropertiesImpl() {
            private static final long serialVersionUID = 4257801713007640581L;
        }).exposeAsDriverPropertyInfoInternal(info, slotsToReserve);
    }

    private boolean autoGenerateTestcaseScriptAsBoolean = false;

    private boolean autoReconnectForPoolsAsBoolean = false;

    private boolean cacheResultSetMetaDataAsBoolean;

    protected boolean characterEncodingIsAliasForSjis = false;

    private boolean jdbcCompliantTruncationForReads = true;

    private boolean highAvailabilityAsBoolean = false;

    private boolean maintainTimeStatsAsBoolean = true;

    private int maxRowsAsInt = -1;

    private boolean useOldUTF8BehaviorAsBoolean = false;

    private boolean useUsageAdvisorAsBoolean = false;

    private boolean profileSQLAsBoolean = false;

    private boolean reconnectTxAtEndAsBoolean = false;

    public JdbcConnectionPropertiesImpl() {
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_holdResultsOpenOverStatementClose));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_allowLoadLocalInfile));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_allowMultiQueries));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_allowNanAndInf));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_allowUrlInLocalInfile));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_alwaysSendSetIsolation));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_autoClosePStmtStreams));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_allowMasterDownConnections));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_autoDeserialize));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_autoGenerateTestcaseScript));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_autoReconnect));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_autoReconnectForPools));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_autoSlowLog));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_blobsAreStrings));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_functionsNeverReturnBlobs));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_cacheCallableStmts));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_cachePrepStmts));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_cacheResultSetMetadata));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_cacheServerConfiguration));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_capitalizeTypeNames));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_clobberStreamingResults));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_compensateOnDuplicateKeyUpdateCounts));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_continueBatchOnError));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_createDatabaseIfNotExist));

        // Think really long and hard about changing the default for this many, many applications have come to be acustomed to the latency profile of preparing
        // stuff client-side, rather than prepare (round-trip), execute (round-trip), close (round-trip).
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useServerPrepStmts));

        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_dontTrackOpenResources));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_dumpQueriesOnException));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_dynamicCalendars));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_elideSetAutoCommits));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_emptyStringsConvertToZero));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_emulateLocators));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_emulateUnsupportedPstmts));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_enablePacketDebug));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_enableQueryTimeouts));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_explainSlowQueries));

        /** When failed-over, set connection to read-only? */
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_failOverReadOnly));

        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_gatherPerfMetrics));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_generateSimpleParameterMetadata));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_includeInnodbStatusInDeadlockExceptions));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_includeThreadDumpInDeadlockExceptions));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_includeThreadNamesAsStatementComment));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_ignoreNonTxTables));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_interactiveClient));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_jdbcCompliantTruncation));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_loadBalanceValidateConnectionOnSwapServer));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_loadBalanceEnableJMX));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_logSlowQueries));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_logXaCommands));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_maintainTimeStats));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_noAccessToProcedureBodies));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_noDatetimeStringSync));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_noTimezoneConversionForTimeType));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_noTimezoneConversionForDateType));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_cacheDefaultTimezone));

        // TODO: rename this property according to WL#8120; default value is already changed as required by this WL
        // TODO: make this property consistent to nullNamePatternMatchesAll; nullCatalogMeansCurrent never cause an exception, but nullNamePatternMatchesAll does.
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_nullCatalogMeansCurrent));

        // TODO: rename this property according to WL#8120; default value is already changed as required by this WL
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_nullNamePatternMatchesAll));

        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_padCharsWithSpace));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_pedantic));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_pinGlobalTxToPhysicalConnection));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_populateInsertRowWithDefaultValues));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_processEscapeCodesForPrepStmts));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_profileSQL));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_queryTimeoutKillsConnection));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_reconnectAtTxEnd));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_relaxAutoCommit));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_requireSSL));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_rewriteBatchedStatements));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_rollbackOnPooledClose));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_roundRobinLoadBalance));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_runningCTS13));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_replicationEnableJMX));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_strictFloatingPoint));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_strictUpdates));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_overrideSupportsIntegrityEnhancementFacility));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_tcpNoDelay));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_tcpKeepAlive));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_tinyInt1isBit));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_traceProtocol));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_treatUtilDateAsTimestamp));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_transformedBitIsBoolean));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useBlobToStoreUTF8OutsideBMP));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useCompression));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useColumnNamesInFindColumn));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useCursorFetch));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useDynamicCharsetInfo));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useDirectRowUnpack));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useFastIntParsing));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useFastDateParsing));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useHostsInPrivileges));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useInformationSchema));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useJDBCCompliantTimezoneShift));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useLocalSessionState));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useLocalTransactionState));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useLegacyDatetimeCode));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useNanosForElapsedTime));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useOldAliasMetadataBehavior));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useOldUTF8Behavior));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useOnlyServerErrorMessages));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useReadAheadInput));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useSSL));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useSSPSCompatibleTimezoneShift));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useStreamLengthsInPrepStmts));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useTimezone));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_ultraDevHack));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useUnbufferedInput));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useUnicode));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useUsageAdvisor));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_yearIsDateType));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useJvmCharsetConverters));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useGmtMillisForDatetimes));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_dumpMetadataOnColumnNotFound));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_useAffectedRows));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_disconnectOnExpiredPasswords));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_getProceduresReturnsFunctions));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_detectCustomCollations));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_allowPublicKeyRetrieval));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_dontCheckOnDuplicateKeyUpdateInSQL));
        addProperty(new BooleanConnectionProperty(PropertyDefinitions.PNAME_readOnlyPropagatesToServer));

        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_callableStmtCacheSize));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_connectTimeout));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_defaultFetchSize));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_initialTimeout));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_loadBalanceBlacklistTimeout));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_loadBalancePingTimeout));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_loadBalanceAutoCommitStatementThreshold));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_maxQuerySizeToLog));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_maxReconnects));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_retriesAllDown));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_maxRows));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_metadataCacheSize));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_netTimeoutForStreamingResults));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_packetDebugBufferSize));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_prepStmtCacheSize));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_prepStmtCacheSqlLimit));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_queriesBeforeRetryMaster));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_reportMetricsIntervalMillis));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_resultSetSizeThreshold));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_secondsBeforeRetryMaster));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_selfDestructOnPingSecondsLifetime));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_selfDestructOnPingMaxOperations));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_slowQueryThresholdMillis));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_socksProxyPort));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_socketTimeout));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_tcpRcvBuf));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_tcpSndBuf));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_tcpTrafficClass));
        addProperty(new IntegerConnectionProperty(PropertyDefinitions.PNAME_maxAllowedPacket));

        addProperty(new LongConnectionProperty(PropertyDefinitions.PNAME_slowQueryThresholdNanos));

        addProperty(new MemorySizeConnectionProperty(PropertyDefinitions.PNAME_blobSendChunkSize));
        addProperty(new MemorySizeConnectionProperty(PropertyDefinitions.PNAME_largeRowSizeThreshold));
        addProperty(new MemorySizeConnectionProperty(PropertyDefinitions.PNAME_locatorFetchBufferSize));

        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_serverConfigCacheFactory));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_characterEncoding));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_characterSetResults));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_connectionAttributes));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_clientInfoProvider));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_clobCharacterEncoding));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_connectionCollation));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_connectionLifecycleInterceptors));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_exceptionInterceptors));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_loadBalanceStrategy));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_loadBalanceConnectionGroup));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_loadBalanceExceptionChecker));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_loadBalanceSQLStateFailover));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_loadBalanceSQLExceptionSubclassFailover));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_loadBalanceAutoCommitStatementRegex));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_localSocketAddress));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_logger));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_parseInfoCacheFactory));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_propertiesTransform));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_resourceId));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_serverTimezone));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_sessionVariables));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_socketFactory));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_socksProxyHost));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_statementInterceptors));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_utf8OutsideBmpExcludedColumnNamePattern));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_utf8OutsideBmpIncludedColumnNamePattern));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_useConfigs));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_zeroDateTimeBehavior));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_authenticationPlugins));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_disabledAuthenticationPlugins));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_defaultAuthenticationPlugin));
        addProperty(new StringConnectionProperty(PropertyDefinitions.PNAME_serverRSAPublicKeyFile));

        this.jdbcCompliantTruncationForReads = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_jdbcCompliantTruncation)).getValueAsBoolean();

    }

    private DriverPropertyInfo getAsDriverPropertyInfo(ConnectionProperty pr) {
        PropertyDefinition pdef = pr.getPropertyDefinition();

        DriverPropertyInfo dpi = new DriverPropertyInfo(pdef.getName(), null);
        dpi.choices = pdef.getAllowableValues();
        dpi.value = (pr.getValue() != null) ? pr.getValue().toString() : null;
        dpi.required = pdef.isRequired();
        dpi.description = pdef.getDescription();

        return dpi;
    }

    protected DriverPropertyInfo[] exposeAsDriverPropertyInfoInternal(Properties info, int slotsToReserve) {
        initializeProperties(info);

        int numProperties = PropertyDefinitions.PROPERTY_NAME_TO_PROPERTY_DEFINITION.keySet().size();

        int listSize = numProperties + slotsToReserve;

        DriverPropertyInfo[] driverProperties = new DriverPropertyInfo[listSize];

        int i = slotsToReserve;

        for (String propName : PropertyDefinitions.PROPERTY_NAME_TO_PROPERTY_DEFINITION.keySet()) {
            ConnectionProperty propToExpose = (ConnectionProperty) getProperty(propName);

            if (info != null) {
                propToExpose.initializeFrom(info, getExceptionInterceptor());
            }

            driverProperties[i++] = getAsDriverPropertyInfo(propToExpose);
        }

        return driverProperties;
    }

    protected Properties exposeAsProperties(Properties info) throws SQLException {
        if (info == null) {
            info = new Properties();
        }

        for (String propName : PropertyDefinitions.PROPERTY_NAME_TO_PROPERTY_DEFINITION.keySet()) {
            ConnectionProperty propToGet = (ConnectionProperty) getProperty(propName);

            Object propValue = propToGet.getValue();

            if (propValue != null) {
                info.setProperty(propToGet.getPropertyDefinition().getName(), propValue.toString());
            }
        }

        return info;
    }

    /**
     * Initializes driver properties that come from a JNDI reference (in the
     * case of a javax.sql.DataSource bound into some name service that doesn't
     * handle Java objects directly).
     * 
     * @param ref
     *            The JNDI Reference that holds RefAddrs for all properties
     * @throws SQLException
     */
    protected void initializeFromRef(Reference ref) throws SQLException {

        for (String propName : PropertyDefinitions.PROPERTY_NAME_TO_PROPERTY_DEFINITION.keySet()) {
            try {
                ConnectionProperty propToSet = (ConnectionProperty) getProperty(propName);

                if (ref != null) {
                    propToSet.initializeFrom(ref, getExceptionInterceptor());
                }
            } catch (Exception e) {
                throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
            }
        }

        postInitialization();
    }

    /**
     * Initializes driver properties that come from URL or properties passed to
     * the driver manager.
     * 
     * @param info
     */
    protected void initializeProperties(Properties info) {
        if (info != null) {
            Properties infoCopy = (Properties) info.clone();

            infoCopy.remove(NonRegisteringDriver.HOST_PROPERTY_KEY);
            infoCopy.remove(NonRegisteringDriver.USER_PROPERTY_KEY);
            infoCopy.remove(NonRegisteringDriver.PASSWORD_PROPERTY_KEY);
            infoCopy.remove(NonRegisteringDriver.DBNAME_PROPERTY_KEY);
            infoCopy.remove(NonRegisteringDriver.PORT_PROPERTY_KEY);

            for (String propName : PropertyDefinitions.PROPERTY_NAME_TO_PROPERTY_DEFINITION.keySet()) {
                try {
                    ConnectionProperty propToSet = (ConnectionProperty) getProperty(propName);
                    propToSet.initializeFrom(infoCopy, getExceptionInterceptor());

                } catch (CJException e) {
                    throw ExceptionFactory.createException(WrongArgumentException.class, e.getMessage(), e, getExceptionInterceptor());
                }
            }

            postInitialization();
        }
    }

    protected void postInitialization() {

        this.reconnectTxAtEndAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_reconnectAtTxEnd)).getValueAsBoolean();

        // Adjust max rows
        if (this.getMaxRows() == 0) {
            // adjust so that it will become MysqlDefs.MAX_ROWS in execSQL()
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_maxRows)).setValueAsObject(Integer.valueOf(-1));
        }

        //
        // Check character encoding
        //
        String testEncoding = this.getEncoding();

        if (testEncoding != null) {
            // Attempt to use the encoding, and bail out if it can't be used
            try {
                String testString = "abc";
                StringUtils.getBytes(testString, testEncoding);
            } catch (UnsupportedEncodingException e) {
                throw ExceptionFactory.createException(WrongArgumentException.class, e.getMessage(), e, getExceptionInterceptor());
            }
        }

        this.cacheResultSetMetaDataAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_cacheResultSetMetadata)).getValueAsBoolean();
        this.useUnicodeAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useUnicode)).getValueAsBoolean();
        this.characterEncodingAsString = getProperty(PropertyDefinitions.PNAME_characterEncoding).getValue(String.class);
        this.highAvailabilityAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_autoReconnect)).getValueAsBoolean();
        this.autoReconnectForPoolsAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_autoReconnectForPools)).getValueAsBoolean();
        this.maxRowsAsInt = ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_maxRows)).getIntValue();
        this.profileSQLAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_profileSQL)).getValueAsBoolean();
        this.useUsageAdvisorAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useUsageAdvisor)).getValueAsBoolean();
        this.useOldUTF8BehaviorAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useOldUTF8Behavior)).getValueAsBoolean();
        this.autoGenerateTestcaseScriptAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_autoGenerateTestcaseScript))
                .getValueAsBoolean();
        this.maintainTimeStatsAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_maintainTimeStats)).getValueAsBoolean();
        this.jdbcCompliantTruncationForReads = getJdbcCompliantTruncation();

        if (getUseCursorFetch()) {
            // assume they want to use server-side prepared statements because they're required for this functionality
            setDetectServerPreparedStmts(true);
        }
    }

    public boolean getAllowLoadLocalInfile() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_allowLoadLocalInfile)).getValueAsBoolean();
    }

    public boolean getAllowMultiQueries() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_allowMultiQueries)).getValueAsBoolean();
    }

    public boolean getAllowNanAndInf() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_allowNanAndInf)).getValueAsBoolean();
    }

    public boolean getAllowUrlInLocalInfile() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_allowUrlInLocalInfile)).getValueAsBoolean();
    }

    public boolean getAlwaysSendSetIsolation() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_alwaysSendSetIsolation)).getValueAsBoolean();
    }

    public boolean getAutoDeserialize() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_autoDeserialize)).getValueAsBoolean();
    }

    public boolean getAutoGenerateTestcaseScript() {
        return this.autoGenerateTestcaseScriptAsBoolean;
    }

    public boolean getAutoReconnectForPools() {
        return this.autoReconnectForPoolsAsBoolean;
    }

    public int getBlobSendChunkSize() {
        return ((MemorySizeConnectionProperty) getProperty(PropertyDefinitions.PNAME_blobSendChunkSize)).getIntValue();
    }

    public boolean getCacheCallableStatements() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_cacheCallableStmts)).getValueAsBoolean();
    }

    public boolean getCachePreparedStatements() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_cachePrepStmts)).getValueAsBoolean();
    }

    public boolean getCacheResultSetMetadata() {
        return this.cacheResultSetMetaDataAsBoolean;
    }

    public boolean getCacheServerConfiguration() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_cacheServerConfiguration)).getValueAsBoolean();
    }

    public int getCallableStatementCacheSize() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_callableStmtCacheSize)).getIntValue();
    }

    public boolean getCapitalizeTypeNames() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_capitalizeTypeNames)).getValueAsBoolean();
    }

    public String getCharacterSetResults() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_characterSetResults)).getValueAsString();
    }

    public String getConnectionAttributes() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_connectionAttributes)).getValueAsString();
    }

    public void setConnectionAttributes(String val) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_connectionAttributes)).setValue(val);
    }

    public boolean getClobberStreamingResults() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_clobberStreamingResults)).getValueAsBoolean();
    }

    public String getClobCharacterEncoding() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_clobCharacterEncoding)).getValueAsString();
    }

    public String getConnectionCollation() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_connectionCollation)).getValueAsString();
    }

    public int getConnectTimeout() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_connectTimeout)).getIntValue();
    }

    public boolean getContinueBatchOnError() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_continueBatchOnError)).getValueAsBoolean();
    }

    public boolean getCreateDatabaseIfNotExist() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_createDatabaseIfNotExist)).getValueAsBoolean();
    }

    public int getDefaultFetchSize() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_defaultFetchSize)).getIntValue();
    }

    public boolean getDontTrackOpenResources() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_dontTrackOpenResources)).getValueAsBoolean();
    }

    public boolean getDumpQueriesOnException() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_dumpQueriesOnException)).getValueAsBoolean();
    }

    public boolean getDynamicCalendars() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_dynamicCalendars)).getValueAsBoolean();
    }

    public boolean getElideSetAutoCommits() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_elideSetAutoCommits)).getValueAsBoolean();
    }

    public boolean getEmptyStringsConvertToZero() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_emptyStringsConvertToZero)).getValueAsBoolean();
    }

    public boolean getEmulateLocators() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_emulateLocators)).getValueAsBoolean();
    }

    public boolean getEmulateUnsupportedPstmts() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_emulateUnsupportedPstmts)).getValueAsBoolean();
    }

    public boolean getEnablePacketDebug() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_enablePacketDebug)).getValueAsBoolean();
    }

    public boolean getExplainSlowQueries() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_explainSlowQueries)).getValueAsBoolean();
    }

    public boolean getFailOverReadOnly() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_failOverReadOnly)).getValueAsBoolean();
    }

    public boolean getGatherPerformanceMetrics() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_gatherPerfMetrics)).getValueAsBoolean();
    }

    protected boolean getHighAvailability() {
        return this.highAvailabilityAsBoolean;
    }

    public boolean getHoldResultsOpenOverStatementClose() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_holdResultsOpenOverStatementClose)).getValueAsBoolean();
    }

    public boolean getIgnoreNonTxTables() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_ignoreNonTxTables)).getValueAsBoolean();
    }

    public int getInitialTimeout() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_initialTimeout)).getIntValue();
    }

    public boolean getInteractiveClient() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_interactiveClient)).getValueAsBoolean();
    }

    public boolean getIsInteractiveClient() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_interactiveClient)).getValueAsBoolean();
    }

    public boolean getJdbcCompliantTruncation() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_jdbcCompliantTruncation)).getValueAsBoolean();
    }

    public int getLocatorFetchBufferSize() {
        return ((MemorySizeConnectionProperty) getProperty(PropertyDefinitions.PNAME_locatorFetchBufferSize)).getIntValue();
    }

    public String getLogger() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_logger)).getValueAsString();
    }

    public String getLoggerClassName() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_logger)).getValueAsString();
    }

    public boolean getLogSlowQueries() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_logSlowQueries)).getValueAsBoolean();
    }

    public boolean getMaintainTimeStats() {
        return this.maintainTimeStatsAsBoolean;
    }

    public int getMaxQuerySizeToLog() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_maxQuerySizeToLog)).getIntValue();
    }

    public int getMaxReconnects() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_maxReconnects)).getIntValue();
    }

    public int getMaxRows() {
        return this.maxRowsAsInt;
    }

    public int getMetadataCacheSize() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_metadataCacheSize)).getIntValue();
    }

    public boolean getNoDatetimeStringSync() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_noDatetimeStringSync)).getValueAsBoolean();
    }

    public boolean getNullCatalogMeansCurrent() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_nullCatalogMeansCurrent)).getValueAsBoolean();
    }

    public boolean getNullNamePatternMatchesAll() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_nullNamePatternMatchesAll)).getValueAsBoolean();
    }

    public int getPacketDebugBufferSize() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_packetDebugBufferSize)).getIntValue();
    }

    public boolean getPedantic() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_pedantic)).getValueAsBoolean();
    }

    public int getPreparedStatementCacheSize() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_prepStmtCacheSize)).getIntValue();
    }

    public int getPreparedStatementCacheSqlLimit() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_prepStmtCacheSqlLimit)).getIntValue();
    }

    public boolean getProfileSQL() {
        return this.profileSQLAsBoolean;
    }

    public String getPropertiesTransform() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_propertiesTransform)).getValueAsString();
    }

    public int getQueriesBeforeRetryMaster() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_queriesBeforeRetryMaster)).getIntValue();
    }

    public boolean getReconnectAtTxEnd() {
        return this.reconnectTxAtEndAsBoolean;
    }

    public boolean getRelaxAutoCommit() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_relaxAutoCommit)).getValueAsBoolean();
    }

    public int getReportMetricsIntervalMillis() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_reportMetricsIntervalMillis)).getIntValue();
    }

    public boolean getRequireSSL() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_requireSSL)).getValueAsBoolean();
    }

    public boolean getRollbackOnPooledClose() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_rollbackOnPooledClose)).getValueAsBoolean();
    }

    public boolean getRoundRobinLoadBalance() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_roundRobinLoadBalance)).getValueAsBoolean();
    }

    public boolean getRunningCTS13() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_runningCTS13)).getValueAsBoolean();
    }

    public int getSecondsBeforeRetryMaster() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_secondsBeforeRetryMaster)).getIntValue();
    }

    public String getServerTimezone() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_serverTimezone)).getValueAsString();
    }

    public String getSessionVariables() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_sessionVariables)).getValueAsString();
    }

    public int getSlowQueryThresholdMillis() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_slowQueryThresholdMillis)).getIntValue();
    }

    public String getSocketFactoryClassName() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_socketFactory)).getValueAsString();
    }

    public int getSocketTimeout() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_socketTimeout)).getIntValue();
    }

    public boolean getStrictFloatingPoint() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_strictFloatingPoint)).getValueAsBoolean();
    }

    public boolean getStrictUpdates() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_strictUpdates)).getValueAsBoolean();
    }

    public boolean getTinyInt1isBit() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_tinyInt1isBit)).getValueAsBoolean();
    }

    public boolean getTraceProtocol() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_traceProtocol)).getValueAsBoolean();
    }

    public boolean getTransformedBitIsBoolean() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_transformedBitIsBoolean)).getValueAsBoolean();
    }

    public boolean getUseCompression() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useCompression)).getValueAsBoolean();
    }

    public boolean getUseFastIntParsing() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useFastIntParsing)).getValueAsBoolean();
    }

    public boolean getUseHostsInPrivileges() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useHostsInPrivileges)).getValueAsBoolean();
    }

    public boolean getUseInformationSchema() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useInformationSchema)).getValueAsBoolean();
    }

    public boolean getUseLocalSessionState() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useLocalSessionState)).getValueAsBoolean();
    }

    public boolean getUseOldUTF8Behavior() {
        return this.useOldUTF8BehaviorAsBoolean;
    }

    public boolean getUseOnlyServerErrorMessages() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useOnlyServerErrorMessages)).getValueAsBoolean();
    }

    public boolean getUseReadAheadInput() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useReadAheadInput)).getValueAsBoolean();
    }

    public boolean getUseServerPreparedStmts() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useServerPrepStmts)).getValueAsBoolean();
    }

    public boolean getUseSSL() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useSSL)).getValueAsBoolean();
    }

    public boolean getUseStreamLengthsInPrepStmts() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useStreamLengthsInPrepStmts)).getValueAsBoolean();
    }

    public boolean getUseTimezone() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useTimezone)).getValueAsBoolean();
    }

    public boolean getUseUltraDevWorkAround() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_ultraDevHack)).getValueAsBoolean();
    }

    public boolean getUseUsageAdvisor() {
        return this.useUsageAdvisorAsBoolean;
    }

    public boolean getYearIsDateType() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_yearIsDateType)).getValueAsBoolean();
    }

    public String getZeroDateTimeBehavior() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_zeroDateTimeBehavior)).getValueAsString();
    }

    public void setAllowLoadLocalInfile(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_allowLoadLocalInfile)).setValue(property);
    }

    public void setAllowMultiQueries(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_allowMultiQueries)).setValue(property);
    }

    public void setAllowNanAndInf(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_allowNanAndInf)).setValue(flag);
    }

    public void setAllowUrlInLocalInfile(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_allowUrlInLocalInfile)).setValue(flag);
    }

    public void setAlwaysSendSetIsolation(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_alwaysSendSetIsolation)).setValue(flag);
    }

    public void setAutoDeserialize(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_autoDeserialize)).setValue(flag);
    }

    public void setAutoGenerateTestcaseScript(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_autoGenerateTestcaseScript)).setValue(flag);
        this.autoGenerateTestcaseScriptAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_autoGenerateTestcaseScript))
                .getValueAsBoolean();
    }

    public void setAutoReconnect(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_autoReconnect)).setValue(flag);
    }

    public void setAutoReconnectForConnectionPools(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_autoReconnectForPools)).setValue(property);
        this.autoReconnectForPoolsAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_autoReconnectForPools)).getValueAsBoolean();
    }

    public void setAutoReconnectForPools(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_autoReconnectForPools)).setValue(flag);
    }

    public void setBlobSendChunkSize(String value) throws SQLException {
        try {
            ((MemorySizeConnectionProperty) getProperty(PropertyDefinitions.PNAME_blobSendChunkSize)).setFromString(value, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setCacheCallableStatements(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_cacheCallableStmts)).setValue(flag);
    }

    public void setCachePreparedStatements(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_cachePrepStmts)).setValue(flag);
    }

    public void setCacheResultSetMetadata(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_cacheResultSetMetadata)).setValue(property);
        this.cacheResultSetMetaDataAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_cacheResultSetMetadata)).getValueAsBoolean();
    }

    public void setCacheServerConfiguration(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_cacheServerConfiguration)).setValue(flag);
    }

    public void setCallableStatementCacheSize(int size) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_callableStmtCacheSize)).setValue(size, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setCapitalizeDBMDTypes(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_capitalizeTypeNames)).setValue(property);
    }

    public void setCapitalizeTypeNames(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_capitalizeTypeNames)).setValue(flag);
    }

    public void setCharacterEncoding(String encoding) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_characterEncoding)).setValue(encoding);
    }

    public void setCharacterSetResults(String characterSet) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_characterSetResults)).setValue(characterSet);
    }

    public void setClobberStreamingResults(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_clobberStreamingResults)).setValue(flag);
    }

    public void setClobCharacterEncoding(String encoding) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_clobCharacterEncoding)).setValue(encoding);
    }

    public void setConnectionCollation(String collation) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_connectionCollation)).setValue(collation);
    }

    public void setConnectTimeout(int timeoutMs) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_connectTimeout)).setValue(timeoutMs, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setContinueBatchOnError(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_continueBatchOnError)).setValue(property);
    }

    public void setCreateDatabaseIfNotExist(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_createDatabaseIfNotExist)).setValue(flag);
    }

    public void setDefaultFetchSize(int n) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_defaultFetchSize)).setValue(n, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setDetectServerPreparedStmts(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useServerPrepStmts)).setValue(property);
    }

    public void setDontTrackOpenResources(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_dontTrackOpenResources)).setValue(flag);
    }

    public void setDumpQueriesOnException(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_dumpQueriesOnException)).setValue(flag);
    }

    public void setDynamicCalendars(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_dynamicCalendars)).setValue(flag);
    }

    public void setElideSetAutoCommits(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_elideSetAutoCommits)).setValue(flag);
    }

    public void setEmptyStringsConvertToZero(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_emptyStringsConvertToZero)).setValue(flag);
    }

    public void setEmulateLocators(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_emulateLocators)).setValue(property);
    }

    public void setEmulateUnsupportedPstmts(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_emulateUnsupportedPstmts)).setValue(flag);
    }

    public void setEnablePacketDebug(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_enablePacketDebug)).setValue(flag);
    }

    public void setEncoding(String property) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_characterEncoding)).setValue(property);
        this.characterEncodingAsString = ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_characterEncoding)).getValueAsString();
    }

    public void setExplainSlowQueries(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_explainSlowQueries)).setValue(flag);
    }

    public void setFailOverReadOnly(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_failOverReadOnly)).setValue(flag);
    }

    public void setGatherPerformanceMetrics(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_gatherPerfMetrics)).setValue(flag);
    }

    protected void setHighAvailability(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_autoReconnect)).setValue(property);
        this.highAvailabilityAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_autoReconnect)).getValueAsBoolean();
    }

    public void setHoldResultsOpenOverStatementClose(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_holdResultsOpenOverStatementClose)).setValue(flag);
    }

    public void setIgnoreNonTxTables(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_ignoreNonTxTables)).setValue(property);
    }

    public void setInitialTimeout(int property) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_initialTimeout)).setValue(property, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setIsInteractiveClient(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_interactiveClient)).setValue(property);
    }

    public void setJdbcCompliantTruncation(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_jdbcCompliantTruncation)).setValue(flag);
    }

    public void setLocatorFetchBufferSize(String value) throws SQLException {
        try {
            ((MemorySizeConnectionProperty) getProperty(PropertyDefinitions.PNAME_locatorFetchBufferSize)).setFromString(value, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setLogger(String property) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_logger)).setValueAsObject(property);
    }

    public void setLoggerClassName(String className) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_logger)).setValue(className);
    }

    public void setLogSlowQueries(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_logSlowQueries)).setValue(flag);
    }

    public void setMaintainTimeStats(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_maintainTimeStats)).setValue(flag);
        this.maintainTimeStatsAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_maintainTimeStats)).getValueAsBoolean();
    }

    public void setMaxQuerySizeToLog(int sizeInBytes) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_maxQuerySizeToLog)).setValue(sizeInBytes, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setMaxReconnects(int property) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_maxReconnects)).setValue(property, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setMaxRows(int property) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_maxRows)).setValue(property, getExceptionInterceptor());
            this.maxRowsAsInt = ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_maxRows)).getIntValue();
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setMetadataCacheSize(int value) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_metadataCacheSize)).setValue(value, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setNoDatetimeStringSync(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_noDatetimeStringSync)).setValue(flag);
    }

    public void setNullCatalogMeansCurrent(boolean value) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_nullCatalogMeansCurrent)).setValue(value);
    }

    public void setNullNamePatternMatchesAll(boolean value) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_nullNamePatternMatchesAll)).setValue(value);
    }

    public void setPacketDebugBufferSize(int size) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_packetDebugBufferSize)).setValue(size, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setPedantic(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_pedantic)).setValue(property);
    }

    public void setPreparedStatementCacheSize(int cacheSize) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_prepStmtCacheSize)).setValue(cacheSize, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setPreparedStatementCacheSqlLimit(int cacheSqlLimit) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_prepStmtCacheSqlLimit)).setValue(cacheSqlLimit, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setProfileSQL(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_profileSQL)).setValue(flag);
        this.profileSQLAsBoolean = flag;
    }

    public void setPropertiesTransform(String value) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_propertiesTransform)).setValue(value);
    }

    public void setQueriesBeforeRetryMaster(int property) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_queriesBeforeRetryMaster)).setValue(property, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setReconnectAtTxEnd(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_reconnectAtTxEnd)).setValue(property);
        this.reconnectTxAtEndAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_reconnectAtTxEnd)).getValueAsBoolean();
    }

    public void setRelaxAutoCommit(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_relaxAutoCommit)).setValue(property);
    }

    public void setReportMetricsIntervalMillis(int millis) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_reportMetricsIntervalMillis)).setValue(millis, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setRequireSSL(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_requireSSL)).setValue(property);
    }

    public void setRollbackOnPooledClose(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_rollbackOnPooledClose)).setValue(flag);
    }

    public void setRoundRobinLoadBalance(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_roundRobinLoadBalance)).setValue(flag);
    }

    public void setRunningCTS13(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_runningCTS13)).setValue(flag);
    }

    public void setSecondsBeforeRetryMaster(int property) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_secondsBeforeRetryMaster)).setValue(property, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setServerTimezone(String property) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_serverTimezone)).setValue(property);
    }

    public void setSessionVariables(String variables) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_sessionVariables)).setValue(variables);
    }

    public void setSlowQueryThresholdMillis(int millis) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_slowQueryThresholdMillis)).setValue(millis, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setSocketFactoryClassName(String property) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_socketFactory)).setValue(property);
    }

    public void setSocketTimeout(int property) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_socketTimeout)).setValue(property, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setStrictFloatingPoint(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_strictFloatingPoint)).setValue(property);
    }

    public void setStrictUpdates(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_strictUpdates)).setValue(property);
    }

    public void setTinyInt1isBit(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_tinyInt1isBit)).setValue(flag);
    }

    public void setTraceProtocol(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_traceProtocol)).setValue(flag);
    }

    public void setTransformedBitIsBoolean(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_transformedBitIsBoolean)).setValue(flag);
    }

    public void setUseCompression(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useCompression)).setValue(property);
    }

    public void setUseFastIntParsing(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useFastIntParsing)).setValue(flag);
    }

    public void setUseHostsInPrivileges(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useHostsInPrivileges)).setValue(property);
    }

    public void setUseInformationSchema(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useInformationSchema)).setValue(flag);
    }

    public void setUseLocalSessionState(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useLocalSessionState)).setValue(flag);
    }

    public void setUseOldUTF8Behavior(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useOldUTF8Behavior)).setValue(flag);
        this.useOldUTF8BehaviorAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useOldUTF8Behavior)).getValueAsBoolean();
    }

    public void setUseOnlyServerErrorMessages(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useOnlyServerErrorMessages)).setValue(flag);
    }

    public void setUseReadAheadInput(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useReadAheadInput)).setValue(flag);
    }

    public void setUseServerPreparedStmts(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useServerPrepStmts)).setValue(flag);
    }

    public void setUseSSL(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useSSL)).setValue(property);
    }

    public void setUseStreamLengthsInPrepStmts(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useStreamLengthsInPrepStmts)).setValue(property);
    }

    public void setUseTimezone(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useTimezone)).setValue(property);
    }

    public void setUseUltraDevWorkAround(boolean property) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_ultraDevHack)).setValue(property);
    }

    public void setUseUnbufferedInput(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useUnbufferedInput)).setValue(flag);
    }

    public void setUseUnicode(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useUnicode)).setValue(flag);
        this.useUnicodeAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useUnicode)).getValueAsBoolean();
    }

    public void setUseUsageAdvisor(boolean useUsageAdvisorFlag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useUsageAdvisor)).setValue(useUsageAdvisorFlag);
        this.useUsageAdvisorAsBoolean = ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useUsageAdvisor)).getValueAsBoolean();
    }

    public void setYearIsDateType(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_yearIsDateType)).setValue(flag);
    }

    public void setZeroDateTimeBehavior(String behavior) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_zeroDateTimeBehavior)).setValue(behavior);
    }

    public boolean useUnbufferedInput() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useUnbufferedInput)).getValueAsBoolean();
    }

    public boolean getUseCursorFetch() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useCursorFetch)).getValueAsBoolean();
    }

    public void setUseCursorFetch(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useCursorFetch)).setValue(flag);
    }

    public boolean getOverrideSupportsIntegrityEnhancementFacility() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_overrideSupportsIntegrityEnhancementFacility)).getValueAsBoolean();
    }

    public void setOverrideSupportsIntegrityEnhancementFacility(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_overrideSupportsIntegrityEnhancementFacility)).setValue(flag);
    }

    public boolean getNoTimezoneConversionForTimeType() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_noTimezoneConversionForTimeType)).getValueAsBoolean();
    }

    public void setNoTimezoneConversionForTimeType(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_noTimezoneConversionForTimeType)).setValue(flag);
    }

    public boolean getNoTimezoneConversionForDateType() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_noTimezoneConversionForDateType)).getValueAsBoolean();
    }

    public void setNoTimezoneConversionForDateType(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_noTimezoneConversionForDateType)).setValue(flag);
    }

    public boolean getCacheDefaultTimezone() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_cacheDefaultTimezone)).getValueAsBoolean();
    }

    public void setCacheDefaultTimezone(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_cacheDefaultTimezone)).setValue(flag);
    }

    public boolean getUseJDBCCompliantTimezoneShift() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useJDBCCompliantTimezoneShift)).getValueAsBoolean();
    }

    public void setUseJDBCCompliantTimezoneShift(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useJDBCCompliantTimezoneShift)).setValue(flag);
    }

    public boolean getAutoClosePStmtStreams() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_autoClosePStmtStreams)).getValueAsBoolean();
    }

    public void setAutoClosePStmtStreams(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_autoClosePStmtStreams)).setValue(flag);
    }

    public boolean getProcessEscapeCodesForPrepStmts() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_processEscapeCodesForPrepStmts)).getValueAsBoolean();
    }

    public void setProcessEscapeCodesForPrepStmts(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_processEscapeCodesForPrepStmts)).setValue(flag);
    }

    public boolean getUseGmtMillisForDatetimes() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useGmtMillisForDatetimes)).getValueAsBoolean();
    }

    public void setUseGmtMillisForDatetimes(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useGmtMillisForDatetimes)).setValue(flag);
    }

    public boolean getDumpMetadataOnColumnNotFound() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_dumpMetadataOnColumnNotFound)).getValueAsBoolean();
    }

    public void setDumpMetadataOnColumnNotFound(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_dumpMetadataOnColumnNotFound)).setValue(flag);
    }

    public String getResourceId() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_resourceId)).getValueAsString();
    }

    public void setResourceId(String resourceId) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_resourceId)).setValue(resourceId);
    }

    public boolean getRewriteBatchedStatements() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_rewriteBatchedStatements)).getValueAsBoolean();
    }

    public void setRewriteBatchedStatements(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_rewriteBatchedStatements)).setValue(flag);
    }

    public boolean getJdbcCompliantTruncationForReads() {
        return this.jdbcCompliantTruncationForReads;
    }

    public void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads) {
        this.jdbcCompliantTruncationForReads = jdbcCompliantTruncationForReads;
    }

    public boolean getUseJvmCharsetConverters() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useJvmCharsetConverters)).getValueAsBoolean();
    }

    public void setUseJvmCharsetConverters(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useJvmCharsetConverters)).setValue(flag);
    }

    public boolean getPinGlobalTxToPhysicalConnection() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_pinGlobalTxToPhysicalConnection)).getValueAsBoolean();
    }

    public void setPinGlobalTxToPhysicalConnection(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_pinGlobalTxToPhysicalConnection)).setValue(flag);
    }

    /*
     * "Aliases" which match the property names to make using
     * from datasources easier.
     */

    public void setGatherPerfMetrics(boolean flag) {
        setGatherPerformanceMetrics(flag);
    }

    public boolean getGatherPerfMetrics() {
        return getGatherPerformanceMetrics();
    }

    public void setUltraDevHack(boolean flag) {
        setUseUltraDevWorkAround(flag);
    }

    public boolean getUltraDevHack() {
        return getUseUltraDevWorkAround();
    }

    public void setInteractiveClient(boolean property) {
        setIsInteractiveClient(property);
    }

    public void setSocketFactory(String name) {
        setSocketFactoryClassName(name);
    }

    public String getSocketFactory() {
        return getSocketFactoryClassName();
    }

    public void setUseServerPrepStmts(boolean flag) {
        setUseServerPreparedStmts(flag);
    }

    public boolean getUseServerPrepStmts() {
        return getUseServerPreparedStmts();
    }

    public void setCacheCallableStmts(boolean flag) {
        setCacheCallableStatements(flag);
    }

    public boolean getCacheCallableStmts() {
        return getCacheCallableStatements();
    }

    public void setCachePrepStmts(boolean flag) {
        setCachePreparedStatements(flag);
    }

    public boolean getCachePrepStmts() {
        return getCachePreparedStatements();
    }

    public void setCallableStmtCacheSize(int cacheSize) throws SQLException {
        setCallableStatementCacheSize(cacheSize);
    }

    public int getCallableStmtCacheSize() {
        return getCallableStatementCacheSize();
    }

    public void setPrepStmtCacheSize(int cacheSize) throws SQLException {
        setPreparedStatementCacheSize(cacheSize);
    }

    public int getPrepStmtCacheSize() {
        return getPreparedStatementCacheSize();
    }

    public void setPrepStmtCacheSqlLimit(int sqlLimit) throws SQLException {
        setPreparedStatementCacheSqlLimit(sqlLimit);
    }

    public int getPrepStmtCacheSqlLimit() {
        return getPreparedStatementCacheSqlLimit();
    }

    public boolean getNoAccessToProcedureBodies() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_noAccessToProcedureBodies)).getValueAsBoolean();
    }

    public void setNoAccessToProcedureBodies(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_noAccessToProcedureBodies)).setValue(flag);
    }

    public boolean getUseOldAliasMetadataBehavior() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useOldAliasMetadataBehavior)).getValueAsBoolean();
    }

    public void setUseOldAliasMetadataBehavior(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useOldAliasMetadataBehavior)).setValue(flag);
    }

    public boolean getUseSSPSCompatibleTimezoneShift() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useSSPSCompatibleTimezoneShift)).getValueAsBoolean();
    }

    public void setUseSSPSCompatibleTimezoneShift(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useSSPSCompatibleTimezoneShift)).setValue(flag);
    }

    public boolean getTreatUtilDateAsTimestamp() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_treatUtilDateAsTimestamp)).getValueAsBoolean();
    }

    public void setTreatUtilDateAsTimestamp(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_treatUtilDateAsTimestamp)).setValue(flag);
    }

    public boolean getUseFastDateParsing() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useFastDateParsing)).getValueAsBoolean();
    }

    public void setUseFastDateParsing(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useFastDateParsing)).setValue(flag);
    }

    public String getLocalSocketAddress() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_localSocketAddress)).getValueAsString();
    }

    public void setLocalSocketAddress(String address) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_localSocketAddress)).setValue(address);
    }

    public void setUseConfigs(String configs) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_useConfigs)).setValue(configs);
    }

    public String getUseConfigs() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_useConfigs)).getValueAsString();
    }

    public boolean getGenerateSimpleParameterMetadata() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_generateSimpleParameterMetadata)).getValueAsBoolean();
    }

    public void setGenerateSimpleParameterMetadata(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_generateSimpleParameterMetadata)).setValue(flag);
    }

    public boolean getLogXaCommands() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_logXaCommands)).getValueAsBoolean();
    }

    public void setLogXaCommands(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_logXaCommands)).setValue(flag);
    }

    public int getResultSetSizeThreshold() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_resultSetSizeThreshold)).getIntValue();
    }

    public void setResultSetSizeThreshold(int threshold) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_resultSetSizeThreshold)).setValue(threshold, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public int getNetTimeoutForStreamingResults() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_netTimeoutForStreamingResults)).getIntValue();
    }

    public void setNetTimeoutForStreamingResults(int value) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_netTimeoutForStreamingResults)).setValue(value, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public boolean getEnableQueryTimeouts() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_enableQueryTimeouts)).getValueAsBoolean();
    }

    public void setEnableQueryTimeouts(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_enableQueryTimeouts)).setValue(flag);
    }

    public boolean getPadCharsWithSpace() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_padCharsWithSpace)).getValueAsBoolean();
    }

    public void setPadCharsWithSpace(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_padCharsWithSpace)).setValue(flag);
    }

    public boolean getUseDynamicCharsetInfo() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useDynamicCharsetInfo)).getValueAsBoolean();
    }

    public void setUseDynamicCharsetInfo(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useDynamicCharsetInfo)).setValue(flag);
    }

    public String getClientInfoProvider() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_clientInfoProvider)).getValueAsString();
    }

    public void setClientInfoProvider(String classname) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_clientInfoProvider)).setValue(classname);
    }

    public boolean getPopulateInsertRowWithDefaultValues() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_populateInsertRowWithDefaultValues)).getValueAsBoolean();
    }

    public void setPopulateInsertRowWithDefaultValues(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_populateInsertRowWithDefaultValues)).setValue(flag);
    }

    public String getLoadBalanceStrategy() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceStrategy)).getValueAsString();
    }

    public void setLoadBalanceStrategy(String strategy) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceStrategy)).setValue(strategy);
    }

    public boolean getTcpNoDelay() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_tcpNoDelay)).getValueAsBoolean();
    }

    public void setTcpNoDelay(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_tcpNoDelay)).setValue(flag);
    }

    public boolean getTcpKeepAlive() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_tcpKeepAlive)).getValueAsBoolean();
    }

    public void setTcpKeepAlive(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_tcpKeepAlive)).setValue(flag);
    }

    public int getTcpRcvBuf() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_tcpRcvBuf)).getIntValue();
    }

    public void setTcpRcvBuf(int bufSize) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_tcpRcvBuf)).setValue(bufSize, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public int getTcpSndBuf() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_tcpSndBuf)).getIntValue();
    }

    public void setTcpSndBuf(int bufSize) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_tcpSndBuf)).setValue(bufSize, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public int getTcpTrafficClass() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_tcpTrafficClass)).getIntValue();
    }

    public void setTcpTrafficClass(int classFlags) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_tcpTrafficClass)).setValue(classFlags, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public boolean getUseNanosForElapsedTime() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useNanosForElapsedTime)).getValueAsBoolean();
    }

    public void setUseNanosForElapsedTime(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useNanosForElapsedTime)).setValue(flag);
    }

    public long getSlowQueryThresholdNanos() {
        return ((LongReadonlyProperty) getProperty(PropertyDefinitions.PNAME_slowQueryThresholdNanos)).getLongValue();
    }

    public void setSlowQueryThresholdNanos(long nanos) throws SQLException {
        try {
            ((LongConnectionProperty) getProperty(PropertyDefinitions.PNAME_slowQueryThresholdNanos)).setValue(nanos, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public String getStatementInterceptors() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_statementInterceptors)).getValueAsString();
    }

    public void setStatementInterceptors(String value) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_statementInterceptors)).setValue(value);
    }

    public boolean getUseDirectRowUnpack() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useDirectRowUnpack)).getValueAsBoolean();
    }

    public void setUseDirectRowUnpack(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useDirectRowUnpack)).setValue(flag);
    }

    public String getLargeRowSizeThreshold() {
        return ((MemorySizeConnectionProperty) getProperty(PropertyDefinitions.PNAME_largeRowSizeThreshold)).getValueAsString();
    }

    public void setLargeRowSizeThreshold(String value) throws SQLException {
        try {
            ((MemorySizeConnectionProperty) getProperty(PropertyDefinitions.PNAME_largeRowSizeThreshold)).setFromString(value, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public boolean getUseBlobToStoreUTF8OutsideBMP() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useBlobToStoreUTF8OutsideBMP)).getValueAsBoolean();
    }

    public void setUseBlobToStoreUTF8OutsideBMP(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useBlobToStoreUTF8OutsideBMP)).setValue(flag);
    }

    public String getUtf8OutsideBmpExcludedColumnNamePattern() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_utf8OutsideBmpExcludedColumnNamePattern)).getValueAsString();
    }

    public void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_utf8OutsideBmpExcludedColumnNamePattern)).setValue(regexPattern);
    }

    public String getUtf8OutsideBmpIncludedColumnNamePattern() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_utf8OutsideBmpIncludedColumnNamePattern)).getValueAsString();
    }

    public void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_utf8OutsideBmpIncludedColumnNamePattern)).setValue(regexPattern);
    }

    public boolean getIncludeInnodbStatusInDeadlockExceptions() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_includeInnodbStatusInDeadlockExceptions)).getValueAsBoolean();
    }

    public void setIncludeInnodbStatusInDeadlockExceptions(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_includeInnodbStatusInDeadlockExceptions)).setValue(flag);
    }

    public boolean getBlobsAreStrings() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_blobsAreStrings)).getValueAsBoolean();
    }

    public void setBlobsAreStrings(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_blobsAreStrings)).setValue(flag);
    }

    public boolean getFunctionsNeverReturnBlobs() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_functionsNeverReturnBlobs)).getValueAsBoolean();
    }

    public void setFunctionsNeverReturnBlobs(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_functionsNeverReturnBlobs)).setValue(flag);
    }

    public boolean getAutoSlowLog() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_autoSlowLog)).getValueAsBoolean();
    }

    public void setAutoSlowLog(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_autoSlowLog)).setValue(flag);
    }

    public String getConnectionLifecycleInterceptors() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_connectionLifecycleInterceptors)).getValueAsString();
    }

    public void setConnectionLifecycleInterceptors(String interceptors) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_connectionLifecycleInterceptors)).setValue(interceptors);
    }

    public boolean getUseLegacyDatetimeCode() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useLegacyDatetimeCode)).getValueAsBoolean();
    }

    public void setUseLegacyDatetimeCode(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useLegacyDatetimeCode)).setValue(flag);
    }

    public int getSelfDestructOnPingSecondsLifetime() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_selfDestructOnPingSecondsLifetime)).getIntValue();
    }

    public void setSelfDestructOnPingSecondsLifetime(int seconds) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_selfDestructOnPingSecondsLifetime)).setValue(seconds, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public int getSelfDestructOnPingMaxOperations() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_selfDestructOnPingMaxOperations)).getIntValue();
    }

    public void setSelfDestructOnPingMaxOperations(int maxOperations) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_selfDestructOnPingMaxOperations)).setValue(maxOperations,
                    getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public boolean getUseColumnNamesInFindColumn() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useColumnNamesInFindColumn)).getValueAsBoolean();
    }

    public void setUseColumnNamesInFindColumn(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useColumnNamesInFindColumn)).setValue(flag);
    }

    public boolean getUseLocalTransactionState() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useLocalTransactionState)).getValueAsBoolean();
    }

    public void setUseLocalTransactionState(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useLocalTransactionState)).setValue(flag);
    }

    public boolean getCompensateOnDuplicateKeyUpdateCounts() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_compensateOnDuplicateKeyUpdateCounts)).getValueAsBoolean();
    }

    public void setCompensateOnDuplicateKeyUpdateCounts(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_compensateOnDuplicateKeyUpdateCounts)).setValue(flag);
    }

    public int getLoadBalanceBlacklistTimeout() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceBlacklistTimeout)).getIntValue();
    }

    public void setLoadBalanceBlacklistTimeout(int loadBalanceBlacklistTimeout) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceBlacklistTimeout)).setValue(loadBalanceBlacklistTimeout,
                    getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public int getLoadBalancePingTimeout() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_loadBalancePingTimeout)).getIntValue();
    }

    public void setLoadBalancePingTimeout(int loadBalancePingTimeout) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalancePingTimeout)).setValue(loadBalancePingTimeout,
                    getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public void setRetriesAllDown(int retriesAllDown) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_retriesAllDown)).setValue(retriesAllDown, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public int getRetriesAllDown() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_retriesAllDown)).getIntValue();
    }

    public void setUseAffectedRows(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useAffectedRows)).setValue(flag);
    }

    public boolean getUseAffectedRows() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_useAffectedRows)).getValueAsBoolean();
    }

    public void setExceptionInterceptors(String exceptionInterceptors) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_exceptionInterceptors)).setValue(exceptionInterceptors);
    }

    public String getExceptionInterceptors() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_exceptionInterceptors)).getValueAsString();
    }

    public void setMaxAllowedPacket(int max) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_maxAllowedPacket)).setValue(max, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public int getMaxAllowedPacket() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_maxAllowedPacket)).getIntValue();
    }

    public boolean getQueryTimeoutKillsConnection() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_queryTimeoutKillsConnection)).getValueAsBoolean();
    }

    public void setQueryTimeoutKillsConnection(boolean queryTimeoutKillsConnection) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_queryTimeoutKillsConnection)).setValue(queryTimeoutKillsConnection);
    }

    public boolean getLoadBalanceValidateConnectionOnSwapServer() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceValidateConnectionOnSwapServer)).getValueAsBoolean();
    }

    public void setLoadBalanceValidateConnectionOnSwapServer(boolean loadBalanceValidateConnectionOnSwapServer) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceValidateConnectionOnSwapServer))
                .setValue(loadBalanceValidateConnectionOnSwapServer);

    }

    public String getLoadBalanceConnectionGroup() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceConnectionGroup)).getValueAsString();
    }

    public void setLoadBalanceConnectionGroup(String loadBalanceConnectionGroup) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceConnectionGroup)).setValue(loadBalanceConnectionGroup);
    }

    public String getLoadBalanceExceptionChecker() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceExceptionChecker)).getValueAsString();
    }

    public void setLoadBalanceExceptionChecker(String loadBalanceExceptionChecker) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceExceptionChecker)).setValue(loadBalanceExceptionChecker);
    }

    public String getLoadBalanceSQLStateFailover() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceSQLStateFailover)).getValueAsString();
    }

    public void setLoadBalanceSQLStateFailover(String loadBalanceSQLStateFailover) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceSQLStateFailover)).setValue(loadBalanceSQLStateFailover);
    }

    public String getLoadBalanceSQLExceptionSubclassFailover() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceSQLExceptionSubclassFailover)).getValueAsString();
    }

    public void setLoadBalanceSQLExceptionSubclassFailover(String loadBalanceSQLExceptionSubclassFailover) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceSQLExceptionSubclassFailover))
                .setValue(loadBalanceSQLExceptionSubclassFailover);
    }

    public boolean getLoadBalanceEnableJMX() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceEnableJMX)).getValueAsBoolean();
    }

    public void setLoadBalanceEnableJMX(boolean loadBalanceEnableJMX) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceEnableJMX)).setValue(loadBalanceEnableJMX);
    }

    public void setLoadBalanceAutoCommitStatementThreshold(int loadBalanceAutoCommitStatementThreshold) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceAutoCommitStatementThreshold)).setValue(
                    loadBalanceAutoCommitStatementThreshold, getExceptionInterceptor());
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public int getLoadBalanceAutoCommitStatementThreshold() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceAutoCommitStatementThreshold)).getIntValue();
    }

    public void setLoadBalanceAutoCommitStatementRegex(String loadBalanceAutoCommitStatementRegex) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceAutoCommitStatementRegex)).setValue(loadBalanceAutoCommitStatementRegex);
    }

    public String getLoadBalanceAutoCommitStatementRegex() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_loadBalanceAutoCommitStatementRegex)).getValueAsString();
    }

    public void setIncludeThreadDumpInDeadlockExceptions(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_includeThreadDumpInDeadlockExceptions)).setValue(flag);
    }

    public boolean getIncludeThreadDumpInDeadlockExceptions() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_includeThreadDumpInDeadlockExceptions)).getValueAsBoolean();
    }

    public void setIncludeThreadNamesAsStatementComment(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_includeThreadNamesAsStatementComment)).setValue(flag);
    }

    public boolean getIncludeThreadNamesAsStatementComment() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_includeThreadNamesAsStatementComment)).getValueAsBoolean();
    }

    public void setAuthenticationPlugins(String authenticationPlugins) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_authenticationPlugins)).setValue(authenticationPlugins);
    }

    public String getAuthenticationPlugins() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_authenticationPlugins)).getValueAsString();
    }

    public void setDisabledAuthenticationPlugins(String disabledAuthenticationPlugins) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_disabledAuthenticationPlugins)).setValue(disabledAuthenticationPlugins);
    }

    public String getDisabledAuthenticationPlugins() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_disabledAuthenticationPlugins)).getValueAsString();
    }

    public void setDefaultAuthenticationPlugin(String defaultAuthenticationPlugin) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_defaultAuthenticationPlugin)).setValue(defaultAuthenticationPlugin);

    }

    public String getDefaultAuthenticationPlugin() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_defaultAuthenticationPlugin)).getValueAsString();
    }

    public void setParseInfoCacheFactory(String factoryClassname) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_parseInfoCacheFactory)).setValue(factoryClassname);
    }

    public String getParseInfoCacheFactory() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_parseInfoCacheFactory)).getValueAsString();
    }

    public void setServerConfigCacheFactory(String factoryClassname) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_serverConfigCacheFactory)).setValue(factoryClassname);
    }

    public String getServerConfigCacheFactory() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_serverConfigCacheFactory)).getValueAsString();
    }

    public void setDisconnectOnExpiredPasswords(boolean disconnectOnExpiredPasswords) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_disconnectOnExpiredPasswords)).setValue(disconnectOnExpiredPasswords);
    }

    public boolean getDisconnectOnExpiredPasswords() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_disconnectOnExpiredPasswords)).getValueAsBoolean();
    }

    public boolean getAllowMasterDownConnections() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_allowMasterDownConnections)).getValueAsBoolean();
    }

    public void setAllowMasterDownConnections(boolean connectIfMasterDown) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_allowMasterDownConnections)).setValue(connectIfMasterDown);
    }

    public boolean getReplicationEnableJMX() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_replicationEnableJMX)).getValueAsBoolean();
    }

    public void setReplicationEnableJMX(boolean replicationEnableJMX) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_replicationEnableJMX)).setValue(replicationEnableJMX);

    }

    public void setGetProceduresReturnsFunctions(boolean getProcedureReturnsFunctions) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_getProceduresReturnsFunctions)).setValue(getProcedureReturnsFunctions);
    }

    public boolean getGetProceduresReturnsFunctions() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_getProceduresReturnsFunctions)).getValueAsBoolean();
    }

    public void setDetectCustomCollations(boolean detectCustomCollations) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_detectCustomCollations)).setValue(detectCustomCollations);
    }

    public boolean getDetectCustomCollations() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_detectCustomCollations)).getValueAsBoolean();
    }

    public void setServerRSAPublicKeyFile(String serverRSAPublicKeyFile) throws SQLException {
        if (((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_serverRSAPublicKeyFile)).getUpdateCount() > 0) {
            throw SQLError.createSQLException(
                    Messages.getString("ConnectionProperties.dynamicChangeIsNotAllowed", new Object[] { "'serverRSAPublicKeyFile'" }),
                    SQLError.SQL_STATE_ILLEGAL_ARGUMENT, null);
        }
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_serverRSAPublicKeyFile)).setValue(serverRSAPublicKeyFile);
    }

    public void setAllowPublicKeyRetrieval(boolean allowPublicKeyRetrieval) throws SQLException {
        if (((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_allowPublicKeyRetrieval)).getUpdateCount() > 0) {
            throw SQLError.createSQLException(
                    Messages.getString("ConnectionProperties.dynamicChangeIsNotAllowed", new Object[] { "'allowPublicKeyRetrieval'" }),
                    SQLError.SQL_STATE_ILLEGAL_ARGUMENT, null);
        }
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_allowPublicKeyRetrieval)).setValue(allowPublicKeyRetrieval);
    }

    public void setDontCheckOnDuplicateKeyUpdateInSQL(boolean dontCheckOnDuplicateKeyUpdateInSQL) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_dontCheckOnDuplicateKeyUpdateInSQL)).setValue(dontCheckOnDuplicateKeyUpdateInSQL);
    }

    public boolean getDontCheckOnDuplicateKeyUpdateInSQL() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_dontCheckOnDuplicateKeyUpdateInSQL)).getValueAsBoolean();
    }

    public void setSocksProxyHost(String socksProxyHost) {
        ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_socksProxyHost)).setValue(socksProxyHost);
    }

    public String getSocksProxyHost() {
        return ((StringConnectionProperty) getProperty(PropertyDefinitions.PNAME_socksProxyHost)).getValueAsString();
    }

    public void setSocksProxyPort(int socksProxyPort) throws SQLException {
        try {
            ((IntegerConnectionProperty) getProperty(PropertyDefinitions.PNAME_socksProxyPort)).setValue(socksProxyPort, null);
        } catch (CJException e) {
            throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, e, getExceptionInterceptor());
        }
    }

    public int getSocksProxyPort() {
        return ((IntegerReadonlyProperty) getProperty(PropertyDefinitions.PNAME_socksProxyPort)).getIntValue();
    }

    public boolean getReadOnlyPropagatesToServer() {
        return ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_readOnlyPropagatesToServer)).getValueAsBoolean();
    }

    public void setReadOnlyPropagatesToServer(boolean flag) {
        ((BooleanConnectionProperty) getProperty(PropertyDefinitions.PNAME_readOnlyPropagatesToServer)).setValue(flag);
    }
}
