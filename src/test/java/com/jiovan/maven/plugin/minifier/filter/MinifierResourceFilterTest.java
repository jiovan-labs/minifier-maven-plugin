/*
 * Copyright (c) 2026 Jiovan Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jiovan.maven.plugin.minifier.filter;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.maven.model.Resource;
import org.apache.maven.shared.filtering.ChangeDetection;
import org.apache.maven.shared.filtering.MavenFileFilter;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sonatype.plexus.build.incremental.DefaultBuildContext;


/**
 * Tests {@link MinifierResourceFilter} against the sample resources under {@code src/test/resources/sample},
 * which is a shrunk copy of the standalone sample project.
 * <p>
 * The filtered result of every test case is written into {@code target/test-output/minifier-resource-filter/&lt;case&gt;}
 * so that it can be inspected after the build.
 *
 * @see MinifierResourceFilter
 */
class MinifierResourceFilterTest {
    /**
     * The files expected to be packaged
     */
    private static final List<String> EXPECTED_FILES = List.of("static/css/plain.css", "static/css/test-css.min.css", "static/js/test-js.min.js");

    @Test
    @DisplayName("Packages the minified js/css instead of the originals when filtering is disabled")
    void prefersMinifiedResourcesWithoutFiltering() throws Exception {
        Path outputDirectory = outputDirectory("filtering-disabled");

        filterResources(outputDirectory, false, null);

        Assertions.assertEquals(EXPECTED_FILES, writtenFiles(outputDirectory));
    }

    @Test
    @DisplayName("Packages the minified js/css instead of the originals when filtering is enabled")
    void prefersMinifiedResourcesWithFiltering() throws Exception {
        Path outputDirectory = outputDirectory("filtering-enabled");

        filterResources(outputDirectory, true, null);

        Assertions.assertEquals(EXPECTED_FILES, writtenFiles(outputDirectory));
    }

    @Test
    @DisplayName("Behaves the same when the file filter is injected instead of created on demand")
    void prefersMinifiedResourcesWithInjectedFileFilter() throws Exception {
        Path outputDirectory = outputDirectory("injected-file-filter");

        filterResources(outputDirectory, false, new MinifierFileFilter(new DefaultBuildContext()));

        Assertions.assertEquals(EXPECTED_FILES, writtenFiles(outputDirectory));
    }

    private void filterResources(Path outputDirectory, boolean filtering, MavenFileFilter fileFilter) throws Exception {
        Path sampleDirectory = sampleDirectory();

        Resource resource = new Resource();
        resource.setDirectory(sampleDirectory.toString());
        resource.setFiltering(filtering);

        MavenResourcesExecution execution = new MavenResourcesExecution();
        execution.setResources(List.of(resource));
        execution.setOutputDirectory(outputDirectory.toFile());
        execution.setResourcesBaseDirectory(sampleDirectory.toFile());
        execution.setEncoding("UTF-8");
        execution.setNonFilteredFileExtensions(new ArrayList<>());
        execution.setFilterWrappers(new ArrayList<>());
        execution.setOverwrite(true);
        execution.setIncludeEmptyDirs(false);
        execution.setAddDefaultExcludes(false);
        execution.setChangeDetection(ChangeDetection.ALWAYS);

        new MinifierResourceFilter(new DefaultBuildContext(), fileFilter).filterResources(execution);
    }

    private Path sampleDirectory() throws Exception {
        return Path.of(Objects.requireNonNull(getClass().getResource("/sample"), "Sample resources are missing").toURI());
    }

    /**
     * Prepares an inspectable output directory under {@code target/test-output}
     *
     * @param name the case name
     *
     * @return the empty output directory
     */
    private Path outputDirectory(String name) throws Exception {
        Path directory = targetDirectory().resolve("test-output").resolve("minifier-resource-filter").resolve(name);
        deleteRecursively(directory);
        return Files.createDirectories(directory);
    }

    private Path targetDirectory() throws Exception {
        return Path.of(MinifierResourceFilter.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
    }

    private void deleteRecursively(Path directory) throws Exception {
        if (!Files.exists(directory)) {
            return;
        }
        try (Stream<Path> stream = Files.walk(directory)) {
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    private List<String> writtenFiles(Path outputDirectory) throws Exception {
        try (Stream<Path> stream = Files.walk(outputDirectory)) {
            return stream.filter(Files::isRegularFile)
                    .map(path -> outputDirectory.relativize(path).toString().replace(File.separatorChar, '/'))
                    .sorted()
                    .toList();
        }
    }
}
