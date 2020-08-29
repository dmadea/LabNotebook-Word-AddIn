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

package com.epam.indigo.eln.tools.users.ui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import com.epam.indigo.eln.tools.users.core.User;
import com.epam.indigo.eln.tools.users.db.DBService;

public class ConsoleUI {

	private static final DBService dbService = new DBService();
	
	private static final PrintStream out = System.out;
	private static final PrintStream err = System.err;
	private static final InputStream in = System.in;
	
	private static final BufferedReader br = new BufferedReader(new InputStreamReader(in));
	
	public static void doConsole() {		
		println("Welcome to Users Tool for Indigo ELN");
		
		println("");
		
		println("Available commands are:");
		println("    list   - show all users");
		println("    show   - show user info");
		println("    edit   - edit user info");
		println("    add    - add new user");
		println("    delete - delete user");
		println("    exit   - return to shell");
		
		println("");
		
		while (true) {
			out.print(": ");
			String cmd = readLine();
			
			if ("exit".equals(cmd))
				break;
			
			if ("list".equals(cmd)) {
				String[] users = getAllUsers();
                for (String user : users) {
                    println(user);
                }
			}
				
			if ("show".equals(cmd)) {
				String username = getUsername();
				User user = getUser(username);
				if (user != null) {
					println("Site code:    " + user.getSitecode());
					println("Full name:    " + user.getFullName());
					println("E-mail:       " + user.getEmail());
					println("Employee id:  " + user.getEmployeeId());
					println("Is Superuser: " + user.isSuperUser());
				} else
					println("User doesn't exists");
			}
				
			if ("edit".equals(cmd)) {
				User user = getUser(getUsername());
				if (user != null) {
					editUser(user);
				} else
					println("User doesn't exists");
			}
			
			if ("add".equals(cmd)) {
				User user = new User();
				user.setUsername(getUsername());
				editUser(user);
			}
			
			if ("delete".equals(cmd)) {
				deleteUser(getUsername());
			}
		}
	}
	
	private static void print(String s, boolean newLine) {
		out.print(s);
		if (newLine)
			out.println();
	}
	
	private static void print(String s) {
		print(s, false);
	}
	
	private static void println(String s) {
		print(s, true);
	}
	
	private static String readLine() {
		try {
			return br.readLine();
		} catch (Exception e) {
			e.printStackTrace(err);
		}
		return null;
	}
	
	private static String getUsername() {
		print("Enter username: ");
		return readLine();
	}

	private static String[] getAllUsers() {
		try {
			return dbService.getAllUsers();
		} catch (Exception e) {
			e.printStackTrace(err);
		}
		return null;
	}
	
	private static User getUser(String username) {
		try {
			return dbService.getUser(username);
		} catch (Exception e) {
			e.printStackTrace(err);
		}
		return null;
	}
	
	private static void updateUser(User user) {
		try {
			dbService.updateUser(user);
			println("User updated");
		} catch (Exception e) {
			e.printStackTrace(err);
		}
	}
	
	private static void deleteUser(String username) {
		try {
			dbService.deleteUser(username);
			println("User deleted");
		} catch (Exception e) {
			e.printStackTrace(err);
		}
	}
		
	private static void editUser(User user) {
		String s;

		println("Leave field blank if you don't want to change field");

		print("Enter new password: ");
		s = readLine();
		if (s.length() > 0)
			user.setPassword(s);

		print("Enter new site code: ");
		s = readLine();
		if (s.length() > 0)
			user.setSitecode(s);

		print("Enter new first name: ");
		s = readLine();
		if (s.length() > 0)
			user.setFirstname(s);

		print("Enter new last name: ");
		s = readLine();
		if (s.length() > 0)
			user.setLastname(s);

		print("Enter new employee id: ");
		s = readLine();
		if (s.length() > 0)
			user.setEmployeeId(s);

		print("Enter new e-mail: ");
		s = readLine();
		if (s.length() > 0)
			user.setEmail(s);

		print("Set superuser flag: ");
		s = readLine();
		if (s.length() > 0)
			user.setSuperUser(Boolean.parseBoolean(s));

		updateUser(user);
	}
}
