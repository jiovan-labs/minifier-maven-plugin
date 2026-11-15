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


import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Strings;
import org.apache.maven.shared.filtering.DefaultMavenResourcesFiltering;
import org.apache.maven.shared.filtering.MavenFileFilter;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;
import org.sonatype.plexus.build.incremental.BuildContext;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;


/**
 * Packages minimum css/js resources instead of original ones
 *
 * @see org.apache.maven.shared.filtering.DefaultMavenResourcesFiltering
 *
 * @reference "https://maven.apache.org/plugins/maven-resources-plugin/examples/custom-resource-filters.html"
 */
@Named(value = MinifierResourceFilter.FILTER_NAME)
@Singleton
@SuppressWarnings({"unused", "JavadocDeclaration", "JavadocLinkAsPlainText"})
public class MinifierResourceFilter implements MavenResourcesFiltering {
    /**
     * The value is {@code "minifierResourceFilter"}
     */
    static final String FILTER_NAME = "minifierResourceFilter";    // $NON-NLS-1$
    private final DefaultMavenResourcesFiltering resourceFilter;

    @Inject
    public MinifierResourceFilter(@Nonnull BuildContext buildContext, @Named(value = MinifierFileFilter.FILTER_NAME) @Nullable MavenFileFilter fileFilter) {
        // The hint based lookup only resolves for maven-filtering 3.5.0+, older realms yield null
        MavenFileFilter minifier = fileFilter != null ? fileFilter : new MinifierFileFilter(buildContext);
        this.resourceFilter = new DefaultMavenResourcesFiltering(minifier, buildContext);
    }

    @Override
    public void filterResources(@Nonnull MavenResourcesExecution execution) throws MavenFilteringException {
        MavenResourcesExecution cloneExecution = execution.copyOf();
        cloneExecution.setUseDefaultFilterWrappers(false);
        resourceFilter.filterResources(cloneExecution);
    }

    @Override
    public boolean filteredFileExtension(@Nonnull String fileName, @Nullable List<String> userNonFilteredFileExtensions) {
        String extension = FilenameUtils.getExtension(fileName);
        return Strings.CI.equalsAny(extension, "js", "css");    // $NON-NLS-1$ // $NON-NLS-2$
    }

    @Override
    public List<String> getDefaultNonFilteredFileExtensions() {
        List<String> extensions = resourceFilter.getDefaultNonFilteredFileExtensions();
        Collections.addAll(extensions, "java", "jar", "class");    // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$
        Collections.addAll(extensions, "txt", "rtf", "log");    // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$
        Collections.addAll(extensions, "zip", "rar", "7z", "tar", "gz");    // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$ // $NON-NLS-4$ // $NON-NLS-5$
        Collections.addAll(extensions, "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf");    // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$ // $NON-NLS-4$ // $NON-NLS-5$ // $NON-NLS-6$ // $NON-NLS-7$
        Collections.addAll(extensions, "less", "tiff", "svg", "ttf", "woff", "woff2");    // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$ // $NON-NLS-4$ // $NON-NLS-5$ // $NON-NLS-6$
        Collections.addAll(extensions, "json", "properties", "xml", "yml", "yaml");    // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$ // $NON-NLS-4$ // $NON-NLS-5$
        return extensions;
    }
}
