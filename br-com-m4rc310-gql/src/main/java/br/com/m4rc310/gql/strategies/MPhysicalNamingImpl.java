package br.com.m4rc310.gql.strategies;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class MPhysicalNamingImpl extends PhysicalNamingStrategyStandardImpl {

	private static final long serialVersionUID = 3926477315094654268L;
	
	/**
	 * To physical sequence name.
	 *
	 * @param name    the name
	 * @param context the context
	 * @return the identifier
	 */
	@Override
	public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment context) {
		return apply(name, context);
	}

	/**
	 * To physical schema name.
	 *
	 * @param name    the name
	 * @param context the context
	 * @return the identifier
	 */
	@Override
	public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment context) {
		return apply(name, context);
	}

	/**
	 * To physical catalog name.
	 *
	 * @param name    the name
	 * @param context the context
	 * @return the identifier
	 */
	@Override
	public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment context) {
		return apply(name, context);
	}

	/**
	 * To physical column name.
	 *
	 * @param name    the name
	 * @param context the context
	 * @return the identifier
	 */
	@Override
	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
		return apply(name, context);
	}

	/**
	 * To physical table name.
	 *
	 * @param name    the name
	 * @param context the context
	 * @return the identifier
	 */
	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
		name = apply(name, context);
		return Identifier.toIdentifier(name.getCanonicalName().toUpperCase(), true);
	}
	
	
	
	/**
	 * Apply.
	 *
	 * @param name    the name
	 * @param context the context
	 * @return the identifier
	 */
	public Identifier apply(Identifier name, JdbcEnvironment context) {
		throw new UnsupportedOperationException("No imlementation for MPhysicalNamingImpl.apply");
	}


}
