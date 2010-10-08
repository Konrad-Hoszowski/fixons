package org.fixons.fixture;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import fit.Fixture;
import fit.Parse;


/**
 * Configuration fixture. Provides means to define init parameters.
 * 
 */
public class JdbcFixture extends Fixture {

    private Logger logger = Logger.getLogger(JdbcFixture.class);

    static final String CMD_INIT = "INIT";
    static final String CMD_EXEC_UPDATE = "EXEC_UPDATE";
    static final String CMD_FAIL_EXEC_UPDATE = "FAIL_EXEC_UPDATE";
    static final String CMD_CLOSE = "CLOSE";
    static final String CMD_SETUP_TEARDOWN = "SETUP_TEARDOWN";

    protected Connection conn;
    private Parse mainCell;

    public JdbcFixture() {
    }

    public JdbcFixture(final String... args) {
        this.args = args;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fit.Fixture#doRow(fit.Parse)
     */
    @Override
    public void doRow(final Parse row) {
        this.mainCell = row.parts;
        final String cmd = this.mainCell.text();
        if (cmd.equalsIgnoreCase(CMD_INIT)) {
            doInit();
        } else if (cmd.equalsIgnoreCase(CMD_EXEC_UPDATE)) {
            doExecUpdate();
        } else if (cmd.equalsIgnoreCase(CMD_FAIL_EXEC_UPDATE)) {
            doFailExecUpdate();
        } else if (cmd.equalsIgnoreCase(CMD_SETUP_TEARDOWN)) {
            doSetUpTearDown();
        } else if (cmd.equalsIgnoreCase(CMD_CLOSE)) {
            doClose();
        } else {
            throw new JdbcFixtureException("unknown command " + cmd);
        }
    }

    private void doClose() {
        closeConnection();
    }

    private void doSetUpTearDown() {
        final String sql = this.mainCell.more.text();
        setUpAndTearDownSQL(sql);
    }

    private void doExecUpdate() {
        final String sql = this.mainCell.more.text();
        executeSQL(sql);
    }

    private void doFailExecUpdate() {
        final String sql = this.mainCell.more.text();
        failExecuteSQL(sql);
    }

    private void doInit() {
        final String url = this.mainCell.more.text();
        final String driver = this.mainCell.more.more.text();
        final String username = this.mainCell.more.more.more.text();
        final String password = this.mainCell.more.more.more.more.text();
        openConnection(url, driver, username, password);
    }

    protected void closeConnection() {
        try {
            if (this.conn.isValid(0)) {
                this.conn.close();
                right(this.mainCell);
            }
        } catch (final SQLException e) {
            wrong(this.mainCell);
            throw new JdbcFixtureException("Cannot close connection: ", e.getMessage());
        }
    }

    protected void setUpAndTearDownSQL(final String sql) {
        final Statement stmt = createStatement();
        try {
            stmt.executeUpdate(sql);
        } catch (final SQLException e) {
        }
    }

    protected void executeSQL(final String sql) {
        final Statement stmt = createStatement();
        try {
            final int retVal = stmt.executeUpdate(sql);
            if (0 == retVal) {
                right(this.mainCell);
            } else {
                wrong(this.mainCell, "" + retVal);
            }
        } catch (final SQLException e) {
            wrong(this.mainCell);
            throw new JdbcFixtureException("Cannot executeUpdate on statement: " + sql + " " + e.getMessage());
        }
    }

    protected void failExecuteSQL(final String sql) {
        final Statement stmt = createStatement();
        try {
            final int retVal = stmt.executeUpdate(sql);
            wrong(this.mainCell);
        } catch (final SQLException e) {
            right(this.mainCell);
        }
    }

    private Statement createStatement() {
        Statement stmt;
        try {
            stmt = this.conn.createStatement();
        } catch (final SQLException e) {
            throw new JdbcFixtureException("Cannot create statement on connection: ", e.getMessage());
        }
        return stmt;
    }

    protected Connection openConnection(final String url, final String driver, final String username, final String password) {
        try {
            Class.forName(driver).newInstance();
            this.conn = DriverManager.getConnection(url, username, password);
        } catch (final InstantiationException e) {
            throw new JdbcFixtureException("Cannot instantiate driver: " + driver, e.getMessage());
        } catch (final IllegalAccessException e) {
            throw new JdbcFixtureException("Cannot access driver class: " + driver, e.getMessage());
        } catch (final ClassNotFoundException e) {
            throw new JdbcFixtureException("Driver class not found: " + driver, e.getMessage());
        } catch (final SQLException e) {
            final String msg = String.format("Cannot open connection: %s, %s, %s", url, username, password);
            throw new JdbcFixtureException(msg, e.getMessage());
        }
        verifyIfConnectionIsValid(this.conn);
        return this.conn;
    }

    private void verifyIfConnectionIsValid(final Connection conn) {
        try {
            if (null != conn && conn.isValid(0)) {
                right(this.mainCell);
            } else {
                wrong(this.mainCell);
            }
        } catch (final SQLException e) {
            throw new JdbcFixtureException("Cannot verify that connection is valid", e.getMessage());
        }
    }
}
