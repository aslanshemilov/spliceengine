/*
 * Copyright (c) 2012 - 2020 Splice Machine, Inc.
 *
 * This file is part of Splice Machine.
 * Splice Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3, or (at your option) any later version.
 * Splice Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with Splice Machine.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.splicemachine.derby.impl.sql.execute.operations;

import com.splicemachine.derby.test.framework.SpliceSchemaWatcher;
import com.splicemachine.derby.test.framework.SpliceUnitTest;
import com.splicemachine.derby.test.framework.SpliceWatcher;
import com.splicemachine.homeless.TestUtils;
import org.junit.*;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zli on 7/6/20.
 * DB-9764
 *
 * Tests that the following queries execute successfully, both when a table exists and does not exist:
 *
 * create [external | (global | local) temporary] table table_name ...
 * create ...                                     table if not exists table_name ...
 * create ...                                     table table_name if not exists ...
 *
 */
public class CreateTableIT extends SpliceUnitTest {
    public static final String CLASS_NAME = CreateTableIT.class.getSimpleName().toUpperCase();
    protected static SpliceWatcher spliceClassWatcher = new SpliceWatcher(CLASS_NAME);
    protected static SpliceSchemaWatcher schema = new SpliceSchemaWatcher(CLASS_NAME);

    @ClassRule
    public static TestRule chain = RuleChain.outerRule(spliceClassWatcher)
            .around(schema);
    @Rule
    public SpliceWatcher methodWatcher = new SpliceWatcher(CLASS_NAME);

    static File tempDir;

    @BeforeClass
    public static void createTempDirectory() throws Exception
    {
        tempDir = createTempDirectory(schema.schemaName);
    }

    @AfterClass
    public static void deleteTempDirectory() throws Exception
    {
        deleteTempDirectory(tempDir);
    }

    private String getExternalResourceDirectory()
    {
        return tempDir.toString() + "/";
    }

    private String appendStorage(String sqlText, String tableType) {
        if (tableType.equals("external")) {
            String path = getExternalResourceDirectory() + "native-spark-table";
            return sqlText + " stored as TEXTFILE location '" + path + "'";
        }
        return sqlText;
    }

    private void testCreateTableIfNotExists(String tableType) throws Exception {
        String tableName = String.format("TEST_CREATE_%s_TABLE_IF_NOT_EXISTS_07061143", tableType.replaceAll(" ", "_").toUpperCase());

        try {
            methodWatcher.executeUpdate(String.format("drop table if exists %s.%s", schema.schemaName, tableName));
            methodWatcher.executeUpdate(appendStorage(String.format("create %s table if not exists %s.%s (c int)", tableType, schema.schemaName, tableName), tableType));

            ResultSet rs = methodWatcher.executeQuery(String.format("select tablename from sys.systables where tablename = '%s'", tableName));
            String s = TestUtils.FormattedResult.ResultFactory.toString(rs);
            Assert.assertTrue(tableName + " has not been created", s.contains(tableName));

            // creating a table with an existing name using "if not exists" returns a warning
            String sqlTexts[] = {
                    "create %s table if not exists %s.%s (c int)",
                    "create %s table %s.%s if not exists (c int)",
                    "create %s table if not exists %s.%s if not exists (c int)"
            };
            try {
                for (String sqlText : sqlTexts) {
                    methodWatcher.executeUpdate(appendStorage(String.format(sqlText, tableType, schema.schemaName, tableName), tableType));
                }
            } catch (SQLException e) {
                Assert.fail("No exception should be thrown");
            }

            // otherwise it's an error
            try {
                methodWatcher.executeUpdate(appendStorage(String.format("create %s table %s.%s (c int)", tableType, schema.schemaName, tableName), tableType));
            } catch (SQLException e) {
                Assert.assertEquals("Wrong Exception", "X0Y32", e.getSQLState());
            }
        }
        finally {
            methodWatcher.executeUpdate(String.format("drop table if exists %s.%s", schema.schemaName, tableName));
        }
    }

    @Test
    public void testCreateTableIfNotExists_BaseTable() throws Exception {
        testCreateTableIfNotExists("");
    }

    @Test
    public void testCreateTableIfNotExists_LocalTempTable() throws Exception {
        testCreateTableIfNotExists("local temporary");
    }

    @Test
    public void testCreateTableIfNotExists_GlobalTempTable() throws Exception {
        testCreateTableIfNotExists("global temporary");
    }

    @Test
    public void testCreateTableIfNotExists_ExternalTable() throws Exception {
        testCreateTableIfNotExists("external");
    }
}
