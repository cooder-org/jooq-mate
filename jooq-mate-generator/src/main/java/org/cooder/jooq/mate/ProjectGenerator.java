package org.cooder.jooq.mate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.cooder.jooq.mate.ConfigurationParser.Config;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProjectGenerator {

    public void generate(Config conf) throws IOException {
        if(conf.projectTemplate() == null || !conf.projectTemplate().exists()) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("artifactId", conf.mateConfig.getArtifactId());
        params.put("groupId", conf.mateConfig.getGroupId());
        params.put("projectDescription", conf.mateConfig.getProjectDescription());
        params.put("datasource.url", conf.jooqConfig.getUrl());
        params.put("datasource.username", conf.jooqConfig.getUser());
        params.put("datasource.password", conf.jooqConfig.getPassword());

        Path destPath = Paths.get(".", "target", "generated-projects", conf.mateConfig.getArtifactId());
        copyDirectory(conf.projectTemplate().getAbsolutePath(), destPath.toString(), params);
        conf.directory(destPath);

    }

    public static void copyDirectory(String sourceDir, String targetDir, Map<String, String> params) throws IOException {
        log.info("copy {} to {}", sourceDir, targetDir);
        Stream<Path> s = Files.walk(Paths.get(sourceDir));
        try {
            s.forEach(file -> {
                String srcPathStr = file.toString();
                Path destination = Paths.get(targetDir, srcPathStr.substring(sourceDir.length()));
                try {
                    log.info("copy {} to {}", file, destination);
                    if(file.toFile().isDirectory()) {
                        destination.toFile().mkdirs();
                    } else {
                        if(srcPathStr.endsWith(".xml") || srcPathStr.endsWith(".yml") || srcPathStr.endsWith(".properties")) {
                            try (FileOutputStream fos = new FileOutputStream(destination.toFile())) {
                                List<String> lines = Files.readAllLines(file);
                                for (String line : lines) {
                                    String newLine = replace(line, params) + "\n";
                                    fos.write(newLine.getBytes());
                                }
                            }
                        } else {
                            Files.copy(file, destination, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } finally {
            s.close();
        }
    }

    private static String replace(String text, Map<String, String> map) {
        return parse("${", "}", text, map);
    }

    public static String parse(String openToken, String closeToken, String text, Map<String, String> map) {
        if(map == null || map.size() == 0) {
            return text;
        }
        if(text == null || text.isEmpty()) {
            return "";
        }
        char[] src = text.toCharArray();
        int offset = 0;
        // search open token
        int start = text.indexOf(openToken, offset);
        if(start == -1) {
            return text;
        }
        final StringBuilder builder = new StringBuilder();
        StringBuilder expression = null;
        while (start > -1) {
            if(start > 0 && src[start - 1] == '\\') {
                // this open token is escaped. remove the backslash and
                // continue.
                builder.append(src, offset, start - offset - 1).append(openToken);
                offset = start + openToken.length();
            } else {
                // found open token. let's search close token.
                if(expression == null) {
                    expression = new StringBuilder();
                } else {
                    expression.setLength(0);
                }
                builder.append(src, offset, start - offset);
                offset = start + openToken.length();
                int end = text.indexOf(closeToken, offset);
                while (end > -1) {
                    if(end > offset && src[end - 1] == '\\') {
                        // this close token is escaped. remove the backslash and
                        // continue.
                        expression.append(src, offset, end - offset - 1).append(closeToken);
                        offset = end + closeToken.length();
                        end = text.indexOf(closeToken, offset);
                    } else {
                        expression.append(src, offset, end - offset);
                        offset = end + closeToken.length();
                        break;
                    }
                }
                if(end == -1) {
                    // close token was not found.
                    builder.append(src, start, src.length - start);
                    offset = src.length;
                } else {
                    String key = text.substring(start + openToken.length(), end);
                    String value = map.get(key);
                    builder.append(value == null ? "" : value.replaceAll("\\\\", "/"));
                    offset = end + closeToken.length();
                }
            }
            start = text.indexOf(openToken, offset);
        }
        if(offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }
        return builder.toString();
    }

}
