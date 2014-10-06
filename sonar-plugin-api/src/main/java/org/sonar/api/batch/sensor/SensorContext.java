/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.api.batch.sensor;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.duplication.DuplicationBuilder;
import org.sonar.api.batch.sensor.duplication.DuplicationGroup;
import org.sonar.api.batch.sensor.duplication.DuplicationTokenBuilder;
import org.sonar.api.batch.sensor.highlighting.HighlightingBuilder;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.batch.sensor.measure.Measure;
import org.sonar.api.batch.sensor.symbol.SymbolTableBuilder;
import org.sonar.api.batch.sensor.test.TestCase;
import org.sonar.api.config.Settings;

import java.io.Serializable;
import java.util.List;

/**
 * See {@link Sensor#execute(SensorContext)}
 * @since 5.0
 */
public interface SensorContext {

  /**
   * Get settings of the current project.
   */
  Settings settings();

  /**
   * Get filesystem of the current project.
   */
  FileSystem fileSystem();

  /**
   * Get list of active rules.
   */
  ActiveRules activeRules();

  // ----------- MEASURES --------------

  /**
   * Fluent builder to create a new {@link Measure}. Don't forget to call {@link Measure#save()} once all parameters are provided.
   */
  <G extends Serializable> Measure<G> newMeasure();

  // ----------- ISSUES --------------

  /**
   * Fluent builder to create a new {@link Issue}. Don't forget to call {@link Issue#save()} once all parameters are provided.
   */
  Issue newIssue();

  // ------------ HIGHLIGHTING ------------

  /**
   * Builder to define highlighting of a file.
   * @since 4.5
   */
  HighlightingBuilder highlightingBuilder(InputFile inputFile);

  // ------------ SYMBOL REFERENCES ------------

  /**
   * Builder to define symbol references in a file.
   * @since 4.5
   */
  SymbolTableBuilder symbolTableBuilder(InputFile inputFile);

  // ------------ DUPLICATIONS ------------

  /**
   * Builder to define tokens in a file. Tokens are used to compute duplication using default SonarQube engine.
   * @since 4.5
   */
  DuplicationTokenBuilder duplicationTokenBuilder(InputFile inputFile);

  /**
   * Builder to manually define duplications in a file. When duplication are manually computed then
   * no need to use {@link #duplicationTokenBuilder(InputFile)}.
   * @since 4.5
   */
  DuplicationBuilder duplicationBuilder(InputFile inputFile);

  /**
   * Register all duplications of an {@link InputFile}. Use {@link #duplicationBuilder(InputFile)} to create
   * list of duplications.
   * @since 4.5
   */
  void saveDuplications(InputFile inputFile, List<DuplicationGroup> duplications);

  // ------------ TESTS ------------

  /**
   * Create a new test case for the given test file.
   * Don't forget to call {@link TestCase#save()} once all parameters are provided.
   * @since 5.0
   */
  TestCase newTestCase();

  /**
   * Register coverage of a given test case on another main file. TestCase should have been registered using {@link #testPlanBuilder(InputFile)}
   * @param testFile test file containing the test case
   * @param testCaseName name of the test case
   * @param coveredFile main file that is covered
   * @param coveredLines list of covered lines
   * @since 5.0
   */
  void saveCoveragePerTest(TestCase testCase, InputFile coveredFile, List<Integer> coveredLines);

  // ------------ DEPENDENCIES ------------

  /**
   * Declare a dependency between 2 files.
   * @param weight Weight of the dependency
   * @since 5.0
   */
  void saveDependency(InputFile from, InputFile to, int weight);

}
