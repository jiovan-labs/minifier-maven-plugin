[English](README.md) | [简体中文](README.zh-CN.md) | [繁體中文](README.zh-TW.md)

# Minifier Maven 插件

玖漫 minifier-maven-plugin 是一个 Maven 插件，用于过滤资源，以压缩后的文件替代原始文件。

> 例如，资源路径下有两个文件 `foo.css` 与 `bar.js`，同时还存在 `foo.min.css`/`foo-min.css` 与 `bar.min.js`/`bar-min.js`（可能由 nodejs 的 `csso-cli` 和 `terser` 生成）。使用此插件后，被打进最终 jar/war 包的是名称中带 `.min`/`-min` 的文件，原始文件不会被打包。

## 快速开始

- 在 `pom.xml` 的 `maven-resources-plugin` 插件下配置即可

```xml

<project>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <version>LATEST</version>
                <configuration>
                    <mavenFilteringHints>
                        <mavenFilteringHint>minifierResourceFilter</mavenFilteringHint>
                    </mavenFilteringHints>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>com.jiovan.maven.plugin</groupId>
                        <artifactId>minifier-maven-plugin</artifactId>
                        <version>LATEST</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
</project>
```

## 文档

- GitHub：https://github.com/jiovan-labs/minifier-maven-plugin

## 环境要求

- jdk 1.8+

## 许可证

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 许可证。

请参阅 `NOTICE.txt` 文件了解所需的声明与归属信息。

## 版权

北京玖漫科技有限公司

## 捐赠

喜欢这个工具包吗？欢迎[向玖漫捐赠](https://jiovan.com/donation)以支持开发。

## 网站

- 玖漫：https://jiovan.com
