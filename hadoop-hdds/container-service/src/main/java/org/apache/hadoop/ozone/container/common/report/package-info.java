/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Datanode Reports: As part of heartbeat, datanode has to share its current
 * state with SCM. The state of datanode is split into multiple reports which
 * are sent along with heartbeat in a configured frequency.
 * <p/>
 * This package contains code which is responsible for sending reports from
 * datanode to SCM.
 * <p/>
 * ReportPublisherFactory: Given a report this constructs corresponding
 * {@link org.apache.hadoop.ozone.container.common.report.ReportPublisher}.
 * <p/>
 * ReportManager: Manages and initializes all the available ReportPublishers.
 * <p/>
 * ReportPublisher: Abstract class responsible for scheduling the reports
 * based on the configured interval. All the ReportPublishers should extend
 * {@link org.apache.hadoop.ozone.container.common.report.ReportPublisher}
 *
 * How to add new report:
 * <p/>
 * <ol>
 * <li>Create a new ReportPublisher class which extends
 * {@link org.apache.hadoop.ozone.container.common.report.ReportPublisher}.</li>
 * <li>Add a mapping Report to ReportPublisher entry in ReportPublisherFactory.</li>
 * <li>In DatanodeStateMachine add the report to ReportManager instance.</li>
 * </ol>
 * <p/>
 * Datanode Reports State Diagram:
 *
 * <pre>
 *   DatanodeStateMachine  ReportManager  ReportPublisher           SCM
 *            |                  |              |                    |
 *            |                  |              |                    |
 *            |    construct     |              |                    |
 *            |-----------------&gt;|              |                    |
 *            |                  |              |                    |
 *            |     init         |              |                    |
 *            |-----------------&gt;|              |                    |
 *            |                  |     init     |                    |
 *            |                  |-------------&gt;|                    |
 *            |                  |              |                    |
 *   +--------+------------------+--------------+--------------------+------+
 *   |loop    |                  |              |                    |      |
 *   |        |                  |   publish    |                    |      |
 *   |        |&lt;-----------------+--------------|                    |      |
 *   |        |                  |   report     |                    |      |
 *   |        |                  |              |                    |      |
 *   |        |                  |              |                    |      |
 *   |        |   heartbeat(rpc) |              |                    |      |
 *   |        |------------------+--------------+-------------------&gt;|      |
 *   |        |                  |              |                    |      |
 *   |        |                  |              |                    |      |
 *   +--------+------------------+--------------+--------------------+------+
 *            |                  |              |                    |
 *            |                  |              |                    |
 *            |                  |              |                    |
 *            |     shutdown     |              |                    |
 *            |-----------------&gt;|              |                    |
 *            |                  |              |                    |
 *            |                  |              |                    |
 *            -                  -              -                    -
 * </pre>
 */
package org.apache.hadoop.ozone.container.common.report;
