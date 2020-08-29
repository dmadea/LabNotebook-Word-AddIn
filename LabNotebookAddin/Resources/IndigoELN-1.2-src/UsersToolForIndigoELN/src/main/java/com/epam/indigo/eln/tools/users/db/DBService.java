/****************************************************************************
 * Copyright (C) 2009-2012 EPAM Systems
 * 
 * This file is part of Indigo ELN.
 * 
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.GPL included in the
 * packaging of this file.
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

package com.epam.indigo.eln.tools.users.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import com.epam.indigo.eln.tools.users.core.User;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;

public class DBService {

	private static final String VALID_STATUS = "VALID";

	private String tableName;

	private Connection conn;

	public DBService() {
		Properties props = new Properties();

		try {
			props.load(DBService.class.getClassLoader().getResourceAsStream("connection.properties"));
		} catch (Exception e) {
			throw new RuntimeException("Could not find 'connection.properties' file!", e);
		}

		this.tableName = props.getProperty("tablename");
	}

	public void close() {
		try {
			if (isConnected()) {
                this.conn.close();
            }
		} catch (Exception ignored) {
			/* Ignored */
		}
	}

	public String[] getAllUsers() throws Exception {
		List<String> users = new ArrayList<String>();

		if (checkConnection()) {
			Statement st = this.conn.createStatement();
			ResultSet rs = st.executeQuery("select username from " + tableName + " order by username asc");

			while (rs.next()) {
				users.add(rs.getString("username"));
			}
		}

		return users.toArray(new String[users.size()]);
	}

	public User getUser(String username) throws Exception {
		User user = null;

		if (checkConnection()) {
			Statement st = this.conn.createStatement();
			ResultSet rs = st.executeQuery("select * from " + tableName + " where username='" + username + "'");

			if (rs.next()) {
				user = new User();

				user.setUsername(rs.getString("username"));
				user.setPassword(StringUtils.EMPTY);
				user.setSitecode(rs.getString("site_code"));

				String fullName = rs.getString("fullname");
				int delimIndex = fullName.indexOf(',');

				if (delimIndex != -1) {
					user.setFirstname(fullName.substring(delimIndex + 1).trim());
					user.setLastname(fullName.substring(0, delimIndex).trim());
				} else {
					user.setFirstname("");
					user.setLastname(fullName);
				}

				user.setEmail(rs.getString("email"));

				user.setXmlMetadata(rs.getString("xml_metadata"));
				user.setMyReagentList(rs.getString("my_reagents"));
				user.setAuditLog(rs.getString("audit_log"));
			}
		}

		return user;
	}

	public void updateUser(User user) throws Exception {
		modifyUser(user);
	}

	public void deleteUser(String username) throws Exception {
		if (checkConnection()) {
			Statement st = this.conn.createStatement();
			st.executeUpdate("delete from " + tableName + " where username = '" + username + "'");
		}
	}

	public boolean userExists(String username) throws Exception {
		if (checkConnection()) {
			Statement st = this.conn.createStatement();
			ResultSet rs = st.executeQuery("select username from " + tableName + " where username='" + username + "'");
			return rs.next();
		}
		return false;
	}

	private void modifyUser(User user) throws Exception {
		if (checkConnection()) {
			boolean passwordNotChanged = false;

			if (userExists(user.getUsername())) {
				if (StringUtils.isBlank(user.getPassword())) {
					user.setPassword(getPassword(user.getUsername()));
					passwordNotChanged = true;
				}

				deleteUser(user.getUsername());
			}

			String fields = "(username, site_code, fullname, email, status, xml_metadata, my_reagents, audit_log)";
			String values = "(?, ?, ?, ?, ?, ?, ?, ?)";

			String sql = "insert into " + tableName + " " + fields + " values " + values;

			PreparedStatement st = this.conn.prepareStatement(sql);

			st.setString(1, user.getUsername());
			st.setString(2, user.getSitecode());
			st.setString(3, user.getLastname() + ", " + user.getFirstname());
			st.setString(4, user.getEmail());
			st.setString(5, VALID_STATUS);
			st.setString(6, user.getXmlMetadata());
			st.setString(7, user.getMyReagentList());
			st.setString(8, user.getAuditLog());

			st.executeUpdate();

			if (StringUtils.isNotBlank(user.getPassword())) {
				st = this.conn.prepareStatement("update "
						+ tableName
						+ " set (password) = ('"
						+ (passwordNotChanged ? user.getPassword()
								: DigestUtils.md5Hex(user.getPassword()))
						+ "') where username = '" + user.getUsername() + "'");
				st.executeUpdate();
			}
		}
	}

	private String getPassword(String username) throws Exception {
		ResultSet rs = this.conn.createStatement().executeQuery("select password from " + tableName + " where username = '" + username + "'");

		if (rs.next()) {
            return rs.getString("password");
        }

		return StringUtils.EMPTY;
	}

	private boolean connect() {
		try {
            DataSource dataSource = (DataSource) new ClassPathXmlApplicationContext("xml/datasource-context.xml").getBean("dataSource");
            this.conn = dataSource.getConnection();
			return isConnected();
		} catch (Exception e) {
			throw new RuntimeException("Could not connect to DB!", e);
		}
	}

	private boolean checkConnection() throws Exception {
        return isConnected() || connect();
    }

	private boolean isConnected() throws Exception {
		return this.conn != null && !this.conn.isClosed();
	}
}
