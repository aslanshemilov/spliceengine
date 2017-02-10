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
 * All such Splice Machine modifications are Copyright 2012 - 2017 Splice Machine, Inc.,
 * and are licensed to you under the GNU Affero General Public License.
 */
package com.splicemachine.dbTesting.system.optimizer.query;

/**
 * 
 * Class Query1: Returns a list of queries that Selects from a single view
 * 
 */

public class Query1 extends GenericQuery {

	public Query1() {
		description = "Select from single view";
		generateQueries();
	}

	/**
	 */
	public void generateQueries() {
		queries
				.add("select col1,col2,col3,col5,col6 from v8 where col4 in ('MYTABLE32_COL4:4122','MYTABLE32_COL4:3419','MYTABLE1_COL4:2197','MYTABLE1_COL4:1243','MYTABLE2_COL4:3684','MYTABLE2_COL4:4264','MYTABLE3_COL4:450','MYTABLE3_COL4:2150','MYTABLE4_COL4:966','MYTABLE4_COL4:2812','MYTABLE5_COL4:4897','MYTABLE5_COL4:2748','MYTABLE6_COL4:245','MYTABLE6_COL4:2341','MYTABLE7_COL4:1603','MYTABLE7_COL4:1150','MYTABLE8_COL4:4759','MYTABLE8_COL4:1535','MYTABLE9_COL4:1227','MYTABLE9_COL4:229','MYTABLE10_COL4:549','MYTABLE10_COL4:4043','MYTABLE11_COL4:1400','MYTABLE11_COL4:3964','MYTABLE12_COL4:3141','MYTABLE12_COL4:2808','MYTABLE13_COL4:2008','MYTABLE13_COL4:3835','MYTABLE14_COL4:3897','MYTABLE14_COL4:246','MYTABLE15_COL4:1284','MYTABLE15_COL4:3715','MYTABLE16_COL4:2583','MYTABLE16_COL4:4507','MYTABLE17_COL4:2899','MYTABLE17_COL4:1670','MYTABLE18_COL4:2187','MYTABLE18_COL4:175','MYTABLE19_COL4:3783','MYTABLE19_COL4:1525','MYTABLE20_COL4:3398','MYTABLE20_COL4:1568','MYTABLE21_COL4:3148','MYTABLE21_COL4:2262','MYTABLE22_COL4:2815','MYTABLE22_COL4:2413','MYTABLE23_COL4:746','MYTABLE23_COL4:4357','MYTABLE24_COL4:1361','MYTABLE24_COL4:564','MYTABLE25_COL4:1427','MYTABLE25_COL4:1568','MYTABLE26_COL4:3707','MYTABLE26_COL4:1986','MYTABLE27_COL4:2771','MYTABLE27_COL4:3322','MYTABLE28_COL4:4485','MYTABLE28_COL4:3905','MYTABLE29_COL4:4142','MYTABLE29_COL4:3812','MYTABLE30_COL4:2724','MYTABLE30_COL4:1380','MYTABLE31_COL4:3702','MYTABLE31_COL4:803' )");
		queries
				.add("select col1,col2,col3,col5,col6 from v16 where col4 in ('MYTABLE32_COL4:4122','MYTABLE32_COL4:3419','MYTABLE1_COL4:2197','MYTABLE1_COL4:1243','MYTABLE2_COL4:3684','MYTABLE2_COL4:4264','MYTABLE3_COL4:450','MYTABLE3_COL4:2150','MYTABLE4_COL4:966','MYTABLE4_COL4:2812','MYTABLE5_COL4:4897','MYTABLE5_COL4:2748','MYTABLE6_COL4:245','MYTABLE6_COL4:2341','MYTABLE7_COL4:1603','MYTABLE7_COL4:1150','MYTABLE8_COL4:4759','MYTABLE8_COL4:1535','MYTABLE9_COL4:1227','MYTABLE9_COL4:229','MYTABLE10_COL4:549','MYTABLE10_COL4:4043','MYTABLE11_COL4:1400','MYTABLE11_COL4:3964','MYTABLE12_COL4:3141','MYTABLE12_COL4:2808','MYTABLE13_COL4:2008','MYTABLE13_COL4:3835','MYTABLE14_COL4:3897','MYTABLE14_COL4:246','MYTABLE15_COL4:1284','MYTABLE15_COL4:3715','MYTABLE16_COL4:2583','MYTABLE16_COL4:4507','MYTABLE17_COL4:2899','MYTABLE17_COL4:1670','MYTABLE18_COL4:2187','MYTABLE18_COL4:175','MYTABLE19_COL4:3783','MYTABLE19_COL4:1525','MYTABLE20_COL4:3398','MYTABLE20_COL4:1568','MYTABLE21_COL4:3148','MYTABLE21_COL4:2262','MYTABLE22_COL4:2815','MYTABLE22_COL4:2413','MYTABLE23_COL4:746','MYTABLE23_COL4:4357','MYTABLE24_COL4:1361','MYTABLE24_COL4:564','MYTABLE25_COL4:1427','MYTABLE25_COL4:1568','MYTABLE26_COL4:3707','MYTABLE26_COL4:1986','MYTABLE27_COL4:2771','MYTABLE27_COL4:3322','MYTABLE28_COL4:4485','MYTABLE28_COL4:3905','MYTABLE29_COL4:4142','MYTABLE29_COL4:3812','MYTABLE30_COL4:2724','MYTABLE30_COL4:1380','MYTABLE31_COL4:3702','MYTABLE31_COL4:803' )");
		queries
				.add("select col1,col2,col3,col5,col6 from v32 where col4 in ('MYTABLE32_COL4:4122','MYTABLE32_COL4:3419','MYTABLE1_COL4:2197','MYTABLE1_COL4:1243','MYTABLE2_COL4:3684','MYTABLE2_COL4:4264','MYTABLE3_COL4:450','MYTABLE3_COL4:2150','MYTABLE4_COL4:966','MYTABLE4_COL4:2812','MYTABLE5_COL4:4897','MYTABLE5_COL4:2748','MYTABLE6_COL4:245','MYTABLE6_COL4:2341','MYTABLE7_COL4:1603','MYTABLE7_COL4:1150','MYTABLE8_COL4:4759','MYTABLE8_COL4:1535','MYTABLE9_COL4:1227','MYTABLE9_COL4:229','MYTABLE10_COL4:549','MYTABLE10_COL4:4043','MYTABLE11_COL4:1400','MYTABLE11_COL4:3964','MYTABLE12_COL4:3141','MYTABLE12_COL4:2808','MYTABLE13_COL4:2008','MYTABLE13_COL4:3835','MYTABLE14_COL4:3897','MYTABLE14_COL4:246','MYTABLE15_COL4:1284','MYTABLE15_COL4:3715','MYTABLE16_COL4:2583','MYTABLE16_COL4:4507','MYTABLE17_COL4:2899','MYTABLE17_COL4:1670','MYTABLE18_COL4:2187','MYTABLE18_COL4:175','MYTABLE19_COL4:3783','MYTABLE19_COL4:1525','MYTABLE20_COL4:3398','MYTABLE20_COL4:1568','MYTABLE21_COL4:3148','MYTABLE21_COL4:2262','MYTABLE22_COL4:2815','MYTABLE22_COL4:2413','MYTABLE23_COL4:746','MYTABLE23_COL4:4357','MYTABLE24_COL4:1361','MYTABLE24_COL4:564','MYTABLE25_COL4:1427','MYTABLE25_COL4:1568','MYTABLE26_COL4:3707','MYTABLE26_COL4:1986','MYTABLE27_COL4:2771','MYTABLE27_COL4:3322','MYTABLE28_COL4:4485','MYTABLE28_COL4:3905','MYTABLE29_COL4:4142','MYTABLE29_COL4:3812','MYTABLE30_COL4:2724','MYTABLE30_COL4:1380','MYTABLE31_COL4:3702','MYTABLE31_COL4:803' )");
		queries
				.add("select col1,col2,col3,col5,col6 from v42 where col4 in ('MYTABLE32_COL4:4122','MYTABLE32_COL4:3419','MYTABLE1_COL4:2197','MYTABLE1_COL4:1243','MYTABLE2_COL4:3684','MYTABLE2_COL4:4264','MYTABLE3_COL4:450','MYTABLE3_COL4:2150','MYTABLE4_COL4:966','MYTABLE4_COL4:2812','MYTABLE5_COL4:4897','MYTABLE5_COL4:2748','MYTABLE6_COL4:245','MYTABLE6_COL4:2341','MYTABLE7_COL4:1603','MYTABLE7_COL4:1150','MYTABLE8_COL4:4759','MYTABLE8_COL4:1535','MYTABLE9_COL4:1227','MYTABLE9_COL4:229','MYTABLE10_COL4:549','MYTABLE10_COL4:4043','MYTABLE11_COL4:1400','MYTABLE11_COL4:3964','MYTABLE12_COL4:3141','MYTABLE12_COL4:2808','MYTABLE13_COL4:2008','MYTABLE13_COL4:3835','MYTABLE14_COL4:3897','MYTABLE14_COL4:246','MYTABLE15_COL4:1284','MYTABLE15_COL4:3715','MYTABLE16_COL4:2583','MYTABLE16_COL4:4507','MYTABLE17_COL4:2899','MYTABLE17_COL4:1670','MYTABLE18_COL4:2187','MYTABLE18_COL4:175','MYTABLE19_COL4:3783','MYTABLE19_COL4:1525','MYTABLE20_COL4:3398','MYTABLE20_COL4:1568','MYTABLE21_COL4:3148','MYTABLE21_COL4:2262','MYTABLE22_COL4:2815','MYTABLE22_COL4:2413','MYTABLE23_COL4:746','MYTABLE23_COL4:4357','MYTABLE24_COL4:1361','MYTABLE24_COL4:564','MYTABLE25_COL4:1427','MYTABLE25_COL4:1568','MYTABLE26_COL4:3707','MYTABLE26_COL4:1986','MYTABLE27_COL4:2771','MYTABLE27_COL4:3322','MYTABLE28_COL4:4485','MYTABLE28_COL4:3905','MYTABLE29_COL4:4142','MYTABLE29_COL4:3812','MYTABLE30_COL4:2724','MYTABLE30_COL4:1380','MYTABLE31_COL4:3702','MYTABLE31_COL4:803' )");

	}

}
