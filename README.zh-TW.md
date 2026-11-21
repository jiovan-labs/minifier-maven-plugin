[English](README.md) | [簡體中文](README.zh-CN.md) | [繁體中文](README.zh-TW.md)

# Minifier Maven 外掛

玖漫 minifier-maven-plugin 是一個 Maven 外掛，用於過濾資源，以壓縮後的檔案替代原始檔案。

> 例如，資源路徑下有兩個檔案 `foo.css` 與 `bar.js`，同時還存在 `foo.min.css`/`foo-min.css` 與 `bar.min.js`/`bar-min.js`（可能由 nodejs 的 `csso-cli` 和 `terser` 生成）。使用此外掛後，被打進最終 jar/war 包的是名稱中帶 `.min`/`-min` 的檔案，原始檔案不會被打包。

## 快速開始

- 在 `pom.xml` 的 `maven-resources-plugin` 外掛下設定即可

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

## 文件

- GitHub：https://github.com/jiovan-labs/minifier-maven-plugin

## 環境要求

- jdk 1.8+

## 許可證

本專案採用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 許可證。

請參閱 `NOTICE.txt` 檔案瞭解所需的宣告與歸屬資訊。

## 版權

北京玖漫科技有限公司

## 捐贈

喜歡這個工具包嗎？歡迎[向玖漫捐贈](https://jiovan.com/donation)以支援開發。

## 網站

- 玖漫：https://jiovan.com
