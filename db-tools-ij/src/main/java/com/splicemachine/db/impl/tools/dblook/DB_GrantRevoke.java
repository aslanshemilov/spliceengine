/*
 * This file is part of Splice Machine.
 * Splice Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3, or (at your option) any later version.
 * Splice Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with Splice Machine.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Some parts of this source code are based on Apache Derby, and the following notices apply to
 * Apache Derby:
 *
 * Apache Derby is a subproject of the Apache DB project, and is licensed under
 * the Apache License, Version 2.0 (the "License"); you may not use these files
 * except in compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Splice Machine, Inc. has modified the Apache Derby code in this file.
 *
 * All such Splice Machine modifications are Copyright 2012 - 2020 Splice Machine, Inc.,
 * and are licensed to you under the GNU Affero General Public License.
 */

package com.splicemachine.db.impl.tools.dblook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.StringTokenizer;

import com.splicemachine.db.tools.dblook;

public class DB_GrantRevoke {

	/** ************************************************
	 * Generate Grant & Revoke statements if sqlAuthorization is on
	 * 
	 * @param conn Connection to use
	 * @param at10_6 True if the databse level is 10.6 or higher
	 */
	public static void doAuthorizations(Connection conn, boolean at10_6)
		throws SQLException {

		// First generate table privilege statements
		Statement stmt = conn.createStatement();
        ResultSet rs;

        if ( at10_6 )
        {
            // Generate udt privilege statements
            rs = stmt.executeQuery("SELECT P.GRANTEE, S.SCHEMANAME, A.ALIAS, P.PERMISSION, P.OBJECTTYPE FROM " +
                                   "SYS.SYSPERMS P, SYS.SYSALIASES A, SYS.SYSSCHEMAS S WHERE A.SCHEMAID = " +
                                   "S.SCHEMAID AND P.OBJECTID = A.ALIASID AND A.ALIASTYPE='A'");
            generateUDTPrivs(rs);
            
            // Generate sequence privilege statements
            rs = stmt.executeQuery("SELECT P.GRANTEE, S.SCHEMANAME, SEQ.SEQUENCENAME, P.PERMISSION, P.OBJECTTYPE FROM " +
                                   "SYS.SYSPERMS P, SYS.SYSSEQUENCES SEQ, SYS.SYSSCHEMAS S WHERE SEQ.SCHEMAID = " +
                                   "S.SCHEMAID AND P.OBJECTID = SEQ.SEQUENCEID");
            generateSequencePrivs(rs);
            // Generate aggregate privilege statements
            rs = stmt.executeQuery("SELECT P.GRANTEE, S.SCHEMANAME, A.ALIAS, P.PERMISSION, P.OBJECTTYPE FROM " +
                                   "SYS.SYSPERMS P, SYS.SYSALIASES A, SYS.SYSSCHEMAS S WHERE A.SCHEMAID = " +
                                   "S.SCHEMAID AND P.OBJECTID = A.ALIASID AND A.ALIASTYPE='G'");
            generateAggregatePrivs(rs);
        }

        rs = stmt.executeQuery("SELECT GRANTEE, SCHEMANAME, TABLENAME, SELECTPRIV, " +
			"DELETEPRIV, INSERTPRIV, UPDATEPRIV, REFERENCESPRIV, TRIGGERPRIV FROM " +
			"SYS.SYSTABLEPERMS P, SYS.SYSTABLES T, SYS.SYSSCHEMAS S WHERE T.SCHEMAID = " +
			"S.SCHEMAID AND T.TABLEID = P.TABLEID");
		generateTablePrivs(rs);

		// Generate column privilege statements
		rs = stmt.executeQuery("SELECT GRANTEE, SCHEMANAME, TABLENAME, TYPE, COLUMNS FROM " +
			"SYS.SYSCOLPERMS P, SYS.SYSTABLES T, SYS.SYSSCHEMAS S WHERE T.SCHEMAID = " +
			"S.SCHEMAID AND T.TABLEID = P.TABLEID");
		generateColumnPrivs(rs, conn);

		// Generate routine privilege statements
		rs = stmt.executeQuery("SELECT GRANTEE, SCHEMANAME, ALIAS, ALIASTYPE FROM " +
			"SYS.SYSROUTINEPERMS P, SYS.SYSALIASES A, SYS.SYSSCHEMAS S WHERE A.SCHEMAID = " +
			"S.SCHEMAID AND P.ALIASID = A.ALIASID");
		generateRoutinePrivs(rs);

		rs.close();
		stmt.close();

	}

	/** ************************************************
	 * Generate table privilege statements
	 * 
	 * @param rs Result set holding required information
	 ****/
	private static void generateTablePrivs(ResultSet rs)
		throws SQLException
	{
		boolean firstTime = true;
		while (rs.next()) {

			if (firstTime) {
				Logs.reportString("----------------------------------------------");
				Logs.reportMessage( "DBLOOK_TablePrivHeader");
				Logs.reportString("----------------------------------------------\n");
			}

			String authName = dblook.addQuotes
				(dblook.expandDoubleQuotes(rs.getString(1)));
			String schemaName = dblook.addQuotes
				(dblook.expandDoubleQuotes(rs.getString(2)));
			String tableName = dblook.addQuotes
				(dblook.expandDoubleQuotes(rs.getString(3)));
			String fullName = schemaName + "." + tableName;

			if (dblook.isIgnorableSchema(schemaName))
				continue;

			Logs.writeToNewDDL(tablePrivStatement(rs, fullName, authName));
			Logs.writeStmtEndToNewDDL();
			Logs.writeNewlineToNewDDL();
			firstTime = false;
		}
	}

	private static String separatorStr(boolean addSeparator)
	{
		return (addSeparator) ? ", " : "";
	}
	
	/** **************************************************
	 * Generate table privilege statement for the current row
	 *
	 * @param rs 		ResultSet holding tableperm information
	 * @param fullName	Table's qualified name
	 * @param authName	Authorization id for grant statement
	 */
	private static String tablePrivStatement(ResultSet rs, String fullName, String authName)
		throws SQLException
	{
		boolean addSeparator = false;
		StringBuilder grantStmt = new StringBuilder("GRANT ");

		if (rs.getString(4).toUpperCase().equals("Y")) 
		{
			grantStmt.append("SELECT");
			addSeparator = true;
		}

		if (rs.getString(5).toUpperCase().equals("Y"))
		{
			grantStmt.append(separatorStr(addSeparator)).append("DELETE");
			addSeparator = true;
		}

		if (rs.getString(6).toUpperCase().equals("Y"))
		{
			grantStmt.append(separatorStr(addSeparator)).append("INSERT");
			addSeparator = true;
		}

		if (rs.getString(7).toUpperCase().equals("Y"))
		{
			grantStmt.append(separatorStr(addSeparator)).append("UPDATE");
			addSeparator = true;
		}

		if (rs.getString(8).toUpperCase().equals("Y"))
		{
			grantStmt.append(separatorStr(addSeparator)).append("REFERENCES");
			addSeparator = true;
		}

		if (rs.getString(9).toUpperCase().equals("Y"))
		{
			grantStmt.append(separatorStr(addSeparator)).append("TRIGGER");
			addSeparator = true;
		}

		grantStmt.append(" ON ").append(fullName).append(" TO ").append(authName);

		return grantStmt.toString();
	}

	/** ************************************************
	 * Generate column privilege statements
	 * 
	 * @param rs	ResultSet holding column privilege information
	 * @param conn	Connection to use. Used to get another ResultSet
	 ****/

	private static void generateColumnPrivs(ResultSet rs, Connection conn)
		throws SQLException
	{
        // Statement that gets the names of the columns in a given table.
        PreparedStatement columnStmt = conn.prepareStatement(
            "SELECT COLUMNNUMBER, COLUMNNAME " +
            "FROM SYS.SYSCOLUMNS C, SYS.SYSTABLES T, SYS.SYSSCHEMAS S " +
            "WHERE T.TABLEID = C.REFERENCEID and S.SCHEMAID = T.SCHEMAID " +
            "AND S.SCHEMANAME = ? AND T.TABLENAME = ? " +
            "ORDER BY COLUMNNUMBER");

		boolean firstTime = true;
		while (rs.next()) {
			if (firstTime) {
				Logs.reportString("----------------------------------------------");
				Logs.reportMessage( "DBLOOK_ColumnPrivHeader");
				Logs.reportString("----------------------------------------------\n");
			}

            // Auth name will added directly to the generated DDL, so we need
            // to quote it.
			String authName = dblook.addQuotes
				(dblook.expandDoubleQuotes(rs.getString(1)));

            // Schema name and table name are parameters to a prepared
            // statement, so quoting is not needed.
            String schemaName = rs.getString(2);
            String tableName = rs.getString(3);

            // isIgnorableSchema, on the other hand, expects the schema name
            // to be quoted.
            String schemaNameQuoted =
                    dblook.addQuotes(dblook.expandDoubleQuotes(schemaName));
            if (dblook.isIgnorableSchema(schemaNameQuoted)) {
				continue;
            }

			// Create another resultSet to get column names
            columnStmt.setString(1, schemaName);
            columnStmt.setString(2, tableName);
            ResultSet rsCols = columnStmt.executeQuery();

            // The full name will be added directly to the generated GRANT
            // statement, so it needs to be quoted.
            String fullName = schemaNameQuoted + "." +
                    dblook.addQuotes(dblook.expandDoubleQuotes(tableName));

			Logs.writeToNewDDL(columnPrivStatement(rs, fullName, authName, rsCols));
			Logs.writeStmtEndToNewDDL();
			Logs.writeNewlineToNewDDL();
			firstTime = false;

            rsCols.close();
		}

        columnStmt.close();
	}

	private static String privTypeToString(String privType)
	{
		switch (privType) {
			case "S":
				return "SELECT";
			case "R":
				return "REFERENCES";
			case "U":
				return "UPDATE";
		}

		// Should throw an exception?
		return "";
	}

	/** ************************************************
	 * Generate one column grant statement
	 * 
	 * @param columns	List of columns to grant required privs
	 * @param rsCols	ResultSet for mapping column numbers to names
	 ****/

	private static String mapColumnsToNames(String columns, ResultSet rsCols)
		throws SQLException
	{
		StringBuilder colNames = new StringBuilder();
		rsCols.next();
		int curColumn = 1;
		boolean addSeparator = false;

		// Strip out outer {} in addition to spaces and comma
		StringTokenizer st = new StringTokenizer(columns, " ,{}");
		while (st.hasMoreTokens())
		{
			int colNum = Integer.parseInt(st.nextToken());
			while (colNum+1 > curColumn)
			{
				rsCols.next();
				curColumn = rsCols.getInt(1);
			}
			colNames.append(separatorStr(addSeparator));
			addSeparator = true;

            String colName = dblook.addQuotes(
                    dblook.expandDoubleQuotes(rsCols.getString(2)));
            colNames.append(colName);
		}

		return colNames.toString();
	}

	/** ************************************************
	 * 
	 * @param rs		ResultSet with info for this GRANT statement
	 * @param fullName	Full qualified name of the table
	 * @param authName	Authorization name for this GRANT
	 * @param rsCols	ResultSet for mapping column numbers to names
	 ****/

	private static String columnPrivStatement(ResultSet rs, String fullName,
			String authName, ResultSet rsCols) throws SQLException
	{
		StringBuilder grantStmt = new StringBuilder("GRANT ");

		String privType = rs.getString(4).toUpperCase();
		String columns = rs.getString(5);
		grantStmt.append(privTypeToString(privType));
		grantStmt.append("(");
		grantStmt.append(mapColumnsToNames(columns, rsCols));
        grantStmt.append(") ON ");
        grantStmt.append(fullName);
		grantStmt.append(" TO ");
		grantStmt.append(authName);

		return grantStmt.toString();
	}

	/** ************************************************
	 * Generate udt privilege statements
	 *
	 * @param rs ResultSet holding required information
	 ****/
	public static void generateUDTPrivs(ResultSet rs) throws SQLException
	{
		boolean firstTime = true;
		while (rs.next()) {
			String authName = dblook.addQuotes
				(dblook.expandDoubleQuotes(rs.getString(1)));
			String schemaName = dblook.addQuotes
				(dblook.expandDoubleQuotes(rs.getString(2)));
			String aliasName = dblook.addQuotes
				(dblook.expandDoubleQuotes(rs.getString(3)));
			String fullName = schemaName + "." + aliasName;
			String permission = rs.getString(4);
			String objectType = rs.getString(5);

			if (dblook.isIgnorableSchema(schemaName))
				continue;

			if (firstTime) {
				Logs.reportString("----------------------------------------------");
				Logs.reportMessage("DBLOOK_UDTPrivHeader");
				Logs.reportString("----------------------------------------------\n");
			}

			Logs.writeToNewDDL(genericPrivStatement(fullName, authName, permission, objectType ));
			Logs.writeStmtEndToNewDDL();
			Logs.writeNewlineToNewDDL();
			firstTime = false;
		}
	}
	/** ************************************************
	 * Generate sequence privilege statements
	 *
	 * @param rs ResultSet holding required information
	 ****/
	public static void generateSequencePrivs(ResultSet rs) throws SQLException
	{
		boolean firstTime = true;
		while (rs.next()) {
			String authName = dblook.addQuotes
				(dblook.expandDoubleQuotes(rs.getString(1)));
			String schemaName = dblook.addQuotes
				(dblook.expandDoubleQuotes(rs.getString(2)));
			String sequenceName = dblook.addQuotes
				(dblook.expandDoubleQuotes(rs.getString(3)));
			String fullName = schemaName + "." + sequenceName;
			String permission = rs.getString(4);
			String objectType = rs.getString(5);

			if (dblook.isIgnorableSchema(schemaName))
				continue;

			if (firstTime) {
				Logs.reportString("----------------------------------------------");
				Logs.reportMessage("DBLOOK_SequencePrivHeader");
				Logs.reportString("----------------------------------------------\n");
			}

			Logs.writeToNewDDL(genericPrivStatement(fullName, authName, permission, objectType ));
			Logs.writeStmtEndToNewDDL();
			Logs.writeNewlineToNewDDL();
			firstTime = false;
		}
	}
	/** ************************************************
	 * Generate aggregate privilege statements
	 *
	 * @param rs ResultSet holding required information
	 ****/
	public static void generateAggregatePrivs(ResultSet rs) throws SQLException
	{
		boolean firstTime = true;
		while (rs.next()) {
			String authName = dblook.addQuotes
				(dblook.expandDoubleQuotes(rs.getString(1)));
			String schemaName = dblook.addQuotes
				(dblook.expandDoubleQuotes(rs.getString(2)));
			String aliasName = dblook.addQuotes
				(dblook.expandDoubleQuotes(rs.getString(3)));
			String fullName = schemaName + "." + aliasName;
			String permission = rs.getString(4);
			String objectType = rs.getString(5);
	
			if (dblook.isIgnorableSchema(schemaName))
				continue;
	
			if (firstTime) {
				Logs.reportString("----------------------------------------------");
				Logs.reportMessage("DBLOOK_AggregatePrivHeader");
				Logs.reportString("----------------------------------------------\n");
			}
	
			Logs.writeToNewDDL(genericPrivStatement(fullName, authName, permission, objectType ));
			Logs.writeStmtEndToNewDDL();
			Logs.writeNewlineToNewDDL();
			firstTime = false;
		}
	}	
	private static String genericPrivStatement(String fullName, String authName, String permission, String objectType )
		throws SQLException
	{
		boolean addSeparator = false;
		String grantStmt = "GRANT " + permission + " ON " + objectType + " " + fullName +
				" TO " +
				authName;

		return grantStmt;
	}

	/** ************************************************
	 * Generate routine privilege statements
	 *
	 * @param rs ResultSet holding required information
	 ****/
	public static void generateRoutinePrivs(ResultSet rs) throws SQLException
	{
		boolean firstTime = true;
		while (rs.next()) {
			String authName = dblook.addQuotes
				(dblook.expandDoubleQuotes(rs.getString(1)));
			String schemaName = dblook.addQuotes
				(dblook.expandDoubleQuotes(rs.getString(2)));
			String aliasName = dblook.addQuotes
				(dblook.expandDoubleQuotes(rs.getString(3)));
			String fullName = schemaName + "." + aliasName;
			String aliasType = rs.getString(4);

			if (dblook.isIgnorableSchema(schemaName))
				continue;

			// Ignore SYSCS_UTIL privileges as all new databases automatically get them
			if (schemaName.equals("\"SYSCS_UTIL\""))
				continue;

			if (firstTime) {
				Logs.reportString("----------------------------------------------");
				Logs.reportMessage("DBLOOK_RoutinePrivHeader");
				Logs.reportString("----------------------------------------------\n");
			}

			Logs.writeToNewDDL(routinePrivStatement(fullName, authName, aliasType));
			Logs.writeStmtEndToNewDDL();
			Logs.writeNewlineToNewDDL();
			firstTime = false;
		}
	}

	private static String routinePrivStatement(String fullName, String authName, String aliasType)
		throws SQLException
	{
		boolean addSeparator = false;
		String grantStmt = "GRANT EXECUTE ON " + ((aliasType.equals("P")) ? "PROCEDURE " : "FUNCTION ") +
				fullName +
				" TO " +
				authName;

		return grantStmt;
	}
}
