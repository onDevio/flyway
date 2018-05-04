/*
 * Copyright 2010-2018 Boxfuse GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flywaydb.core.internal.database.sybasease11;

import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.errorhandler.ErrorHandler;
import org.flywaydb.core.api.logging.Log;
import org.flywaydb.core.api.logging.LogFactory;
import org.flywaydb.core.internal.database.Database;
import org.flywaydb.core.internal.database.Delimiter;
import org.flywaydb.core.internal.database.SqlScript;
import org.flywaydb.core.internal.exception.FlywayDbUpgradeRequiredException;
import org.flywaydb.core.internal.util.PlaceholderReplacer;
import org.flywaydb.core.internal.util.scanner.LoadableResource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Sybase ASE database.
 */
public class SybaseASEDatabase extends Database<SybaseASEConnection> {
    private final boolean jconnect;
    
    private static final Log LOG = LogFactory.getLog(Database.class);



    /**
     * Creates a new Sybase ASE database.
     *
     * @param configuration The Flyway configuration.
     * @param connection    The initial connection.
     * @param jconnect      Whether we are using the official jConnect driver or not (jTDS).
     */
    public SybaseASEDatabase(Configuration configuration, Connection connection, boolean jconnect



    ) {
        super(configuration, connection



        );
        this.jconnect = jconnect;
    }

    @Override
    protected SybaseASEConnection getConnection(Connection connection



    ) {
        return new SybaseASEConnection(configuration, this, connection, jconnect



        );
    }

    @Override
    protected void ensureSupported() {
    	// TODO Think if version checking is needed, otherwise remove this block
        String version = majorVersion + "." + minorVersion;
        /*

        if (majorVersion < 15 || (majorVersion == 15 && minorVersion < 7)) {
            throw new FlywayDbUpgradeRequiredException("Sybase ASE", version, "15.7");
        }
        if (majorVersion > 16 || (majorVersion == 16 && minorVersion > 2)) {
            recommendFlywayUpgrade("Sybase ASE", version);
        }
        */
    	LOG.warn("Flyway support form Sybase ASE version " + version + " is limited (e.g. no DDL transactions)");
    }

    @Override
    protected SqlScript doCreateSqlScript(LoadableResource sqlScriptResource,
                                          PlaceholderReplacer placeholderReplacer, boolean mixed



    ) {
        return new SybaseASESqlScript(sqlScriptResource, placeholderReplacer, mixed



        );
    }

    @Override
    public Delimiter getDefaultDelimiter() {
        return new Delimiter("GO", true);
    }

    @Override
    public String getDbName() {
        return "sybasease11";
    }

    @Override
    protected String doGetCurrentUser() throws SQLException {
        return getMainConnection().getJdbcTemplate().queryForString("SELECT user_name()");
    }

    @Override
    public boolean supportsDdlTransactions() {
        return false;
    }

    @Override
    protected boolean supportsChangingCurrentSchema() {
        return true;
    }

    @Override
    public String getBooleanTrue() {
        return "1";
    }

    @Override
    public String getBooleanFalse() {
        return "0";
    }

    @Override
    protected String doQuote(String identifier) {
        //Sybase doesn't quote identifiers, skip quoting.
        return identifier;
    }

    @Override
    public boolean catalogIsSchema() {
        return false;
    }
}