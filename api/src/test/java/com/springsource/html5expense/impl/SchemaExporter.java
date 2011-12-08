package com.springsource.html5expense.impl;

import java.io.File;
import java.sql.Driver;

import org.apache.commons.lang.SystemUtils;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import com.springsource.html5expense.EligibleCharge;
import com.springsource.html5expense.Expense;
import com.springsource.html5expense.ExpenseReport;

public class SchemaExporter {

	static private Class<?>[] entityClasses = { Expense.class,
			ExpenseReport.class, EligibleCharge.class };

	static private void generate(Class<? extends Dialect> dialectClazz,
			Class<? extends Driver> driverClazz, Class<?>[] clazzes, String user,
			String pw, String url) {

		org.hibernate.cfg.Configuration ac = new org.hibernate.cfg.Configuration();

		ac.setProperty("hibernate.dialect", dialectClazz.getName());
		ac.setProperty("hibernate.connection.driver_class", driverClazz.getName());
		ac.setProperty("hibernate.connection.url", url);
		ac.setProperty("hibernate.connection.username", user);
		ac.setProperty("hibernate.connection.password", pw);

		for (Class<?> c : clazzes)
			ac.addAnnotatedClass(c);

		File out = new File(new File(SystemUtils.getUserHome(), "Desktop"),
				"ddl" + dialectClazz.getSimpleName() + ".sql");

		SchemaExport export = new SchemaExport(ac);
		export.setDelimiter(";");
		export.setOutputFile(out.getAbsolutePath());
		export.execute(true, false, false, false);
	}

	static public void main(String[] args) throws Throwable {

		generate(MySQL5InnoDBDialect.class, com.mysql.jdbc.Driver.class,
				entityClasses, "expenses", "expenses",
				"jdbc:mysql://localhost/expenses");

	}
}
