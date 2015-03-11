/*

   Derby - Class org.apache.derbyTesting.unitTests.lang.EmptyResultSetStatisticsFactory

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */

package org.apache.derbyTesting.unitTests.lang;

import com.splicemachine.db.iapi.error.StandardException;


import com.splicemachine.db.iapi.sql.Activation;
import com.splicemachine.db.iapi.sql.ResultSet;

import com.splicemachine.db.iapi.sql.execute.NoPutResultSet;
import com.splicemachine.db.iapi.sql.execute.ResultSetStatisticsFactory;

import com.splicemachine.db.iapi.sql.execute.RunTimeStatistics;

import com.splicemachine.db.iapi.sql.execute.ResultSetStatistics;

/**
 * ResultSetStatisticsFactory provides a wrapper around all of
 * objects associated with run time statistics.
 * <p>
 * This implementation of the protocol is for stubbing out
 * the RunTimeStatistics feature at execution time..
 *
 */
public class EmptyResultSetStatisticsFactory 
		implements ResultSetStatisticsFactory
{
	//
	// ExecutionFactory interface
	//
	//
	// ResultSetStatisticsFactory interface
	//

	/**
		@see ResultSetStatisticsFactory#getRunTimeStatistics
	 */
	public RunTimeStatistics getRunTimeStatistics(
			Activation activation, 
			ResultSet rs,
			NoPutResultSet[] subqueryTrackingArray)
		throws StandardException
	{
		return null;
	}

	/**
		@see ResultSetStatisticsFactory#getResultSetStatistics
	 */
	public ResultSetStatistics getResultSetStatistics(ResultSet rs)
	{
		return null;
	}

	/**
		@see ResultSetStatisticsFactory#getResultSetStatistics
	 */
	public ResultSetStatistics getResultSetStatistics(NoPutResultSet rs)
	{
		return null;
	}

	/**
		@see ResultSetStatisticsFactory#getNoRowsResultSetStatistics
	 */
	public ResultSetStatistics getNoRowsResultSetStatistics(ResultSet rs)
	{
		return null;
	}

	//
	// class interface
	//
	public EmptyResultSetStatisticsFactory() 
	{
	}

}
