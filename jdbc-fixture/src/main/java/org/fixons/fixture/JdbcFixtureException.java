package org.fixons.fixture;

import fit.exception.FixtureException;

public class JdbcFixtureException extends FixtureException {

    private static final long serialVersionUID = 1256211646124015941L;

    public JdbcFixtureException(final String message) {
        super(message, "Jdbc Fixture");
    }

    public JdbcFixtureException(final String message, final String exMsg) {
        super(message, exMsg);
    }

}
