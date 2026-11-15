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


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


/**
 * Guards the sisu component index, without it the components are never discovered at runtime
 *
 * @see <a href="https://eclipse.dev/sisu/">Sisu</a>
 */
class ComponentIndexTest {
    @Test
    @DisplayName("The sisu index registers both filter components")
    void registersBothComponents() throws Exception {
        Path index = classesDirectory().resolve("META-INF").resolve("sisu").resolve("javax.inject.Named");

        Assertions.assertTrue(Files.isRegularFile(index), "Missing sisu index: " + index);

        List<String> lines = Files.readAllLines(index);
        Assertions.assertTrue(lines.contains(MinifierFileFilter.class.getName()), "MinifierFileFilter is not indexed");
        Assertions.assertTrue(lines.contains(MinifierResourceFilter.class.getName()), "MinifierResourceFilter is not indexed");
    }

    private Path classesDirectory() throws Exception {
        return Path.of(MinifierFileFilter.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    }
}
