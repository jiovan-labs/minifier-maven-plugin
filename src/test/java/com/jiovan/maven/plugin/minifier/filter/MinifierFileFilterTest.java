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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.maven.shared.filtering.ChangeDetection;
import org.apache.maven.shared.filtering.FilterWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sonatype.plexus.build.incremental.DefaultBuildContext;


/**
 * Tests the copying decision of {@link MinifierFileFilter}
 *
 * @see MinifierFileFilter
 */
class MinifierFileFilterTest {
    @TempDir
    Path directory;

    private File output;

    private MinifierFileFilter fileFilter;

    @BeforeEach
    void setUp() throws IOException {
        output = Files.createDirectories(directory.resolve("output")).toFile();
        fileFilter = new MinifierFileFilter(new DefaultBuildContext());
    }

    @Test
    @DisplayName("Excludes the original js file when a .min.js sibling exists")
    void excludesOriginalJsWhenDotMinSiblingExists() throws Exception {
        File original = createFile("test-js.js");
        createFile("test-js.min.js");

        copyFile(original);

        assertNotCopied(original);
    }

    @Test
    @DisplayName("Excludes the original css file when a -min.css sibling exists")
    void excludesOriginalCssWhenHyphenMinSiblingExists() throws Exception {
        File original = createFile("test-css.css");
        createFile("test-css-min.css");

        copyFile(original);

        assertNotCopied(original);
    }

    @Test
    @DisplayName("Copies the original file when no minified sibling exists")
    void copiesOriginalWithoutMinifiedSibling() throws Exception {
        File original = createFile("plain.css");

        copyFile(original);

        assertCopied(original, "plain.css");
    }

    @Test
    @DisplayName("Copies the already minified file itself")
    void copiesMinifiedFile() throws Exception {
        File minified = createFile("test-js.min.js");

        copyFile(minified);

        assertCopied(minified, "test-js.min.js");
    }

    @Test
    @DisplayName("Copies files of other extensions without checking siblings")
    void copiesFilesWithOtherExtensions() throws Exception {
        File readme = createFile("readme.txt");
        createFile("readme.min.txt");

        copyFile(readme);

        assertCopied(readme, "readme.txt");
    }

    @Test
    @DisplayName("Removes the previously copied target when the original has to be excluded")
    void removesPreviouslyCopiedTarget() throws Exception {
        File original = createFile("test-js.js");
        createFile("test-js.min.js");
        Files.writeString(targetOf(original).toPath(), "previously copied", StandardCharsets.UTF_8);

        copyFile(original);

        assertNotCopied(original);
    }

    @Test
    @DisplayName("Excludes the original file through the change detection overload")
    void excludesOriginalThroughChangeDetectionOverload() throws Exception {
        File original = createFile("test-js.js");
        createFile("test-js.min.js");

        fileFilter.copyFile(original, targetOf(original), false, List.<FilterWrapper>of(), "UTF-8", ChangeDetection.ALWAYS);

        assertNotCopied(original);
    }

    private File createFile(String name) throws IOException {
        Path path = directory.resolve(name);
        Files.writeString(path, "content of " + name, StandardCharsets.UTF_8);
        return path.toFile();
    }

    private File targetOf(File from) {
        return new File(output, from.getName());
    }

    private void copyFile(File from) throws Exception {
        fileFilter.copyFile(from, targetOf(from), false, List.<FilterWrapper>of(), "UTF-8", true);
    }

    private void assertCopied(File from, String targetName) throws IOException {
        File target = new File(output, targetName);
        Assertions.assertTrue(target.isFile(), "Expected " + targetName + " to be copied");
        Assertions.assertEquals("content of " + from.getName(), Files.readString(target.toPath(), StandardCharsets.UTF_8).trim());
    }

    private void assertNotCopied(File from) {
        File target = targetOf(from);
        Assertions.assertFalse(target.exists(), "Expected " + target.getName() + " to be excluded, but it exists");
    }
}
