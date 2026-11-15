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
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.maven.shared.filtering.ChangeDetection;
import org.apache.maven.shared.filtering.DefaultMavenFileFilter;
import org.apache.maven.shared.filtering.FilterWrapper;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.sonatype.plexus.build.incremental.BuildContext;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;


/**
 * Packages minimum css/js files instead of original ones
 *
 * @see org.apache.maven.shared.filtering.DefaultMavenFileFilter
 *
 * @reference "https://github.com/aperto/sourceurl-resourcefilter"
 */
@Named(value = MinifierFileFilter.FILTER_NAME)
@Singleton
@SuppressWarnings({"unused", "JavadocDeclaration", "JavadocLinkAsPlainText"})
public class MinifierFileFilter extends DefaultMavenFileFilter {
    /**
     * The value is {@code "minifierFileFilter"}
     */
    static final String FILTER_NAME = "minifierFileFilter";    // $NON-NLS-1$
    private static final String DOT_MIN = ".min";    // $NON-NLS-1$
    private static final String HYPHEN_MIN = "-min";    // $NON-NLS-1$

    @Inject
    public MinifierFileFilter(@Nonnull BuildContext context) {
        super(context);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void copyFile(@Nonnull File from, @Nonnull File to, boolean filtering, @Nonnull List<FilterWrapper> filterWrappers, @Nullable String encoding, boolean overwrite) throws MavenFilteringException {
        if (shouldCopy(from)) {
            super.copyFile(from, to, filtering, filterWrappers, encoding, overwrite);
        } else {
            FileUtils.deleteQuietly(to);
        }
    }

    @Override
    public void copyFile(@Nonnull File from, @Nonnull File to, boolean filtering, @Nonnull List<FilterWrapper> filterWrappers, @Nullable String encoding, @Nullable ChangeDetection changeDetection) throws MavenFilteringException {
        if (shouldCopy(from)) {
            super.copyFile(from, to, filtering, filterWrappers, encoding, changeDetection);
        } else {
            FileUtils.deleteQuietly(to);
        }
    }

    /**
     * Checks whether the original file has to be copied, that is there is no minified sibling
     *
     * @param from the original file
     * @return {@code true} unless a {@code .min} or {@code -min} sibling of a css/js file exists
     */
    private static boolean shouldCopy(@Nonnull File from) {
        String extension = FilenameUtils.getExtension(from.getName());
        if (!from.isFile() || !Strings.CI.equalsAny(extension, "js", "css")) {    // $NON-NLS-1$ // $NON-NLS-2$
            return true;
        }
        String basename = FilenameUtils.getBaseName(from.getName());
        if (Strings.CI.endsWith(basename, DOT_MIN) || Strings.CI.endsWith(basename, HYPHEN_MIN)) {
            return true;
        }
        File parent = from.getParentFile();
        File dotFrom = new File(parent, StringUtils.join(basename, DOT_MIN, FilenameUtils.EXTENSION_SEPARATOR_STR, extension));
        File minusFrom = new File(parent, StringUtils.join(basename, HYPHEN_MIN, FilenameUtils.EXTENSION_SEPARATOR_STR, extension));
        try {
            return !FileUtils.directoryContains(parent, dotFrom) && !FileUtils.directoryContains(parent, minusFrom);
        } catch (Exception ignored) {
            return true;
        }
    }
}
