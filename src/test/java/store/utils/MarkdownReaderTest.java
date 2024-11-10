package store.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class MarkdownReaderTest {
    @Test
    void 리소스파일을_올바르게_읽어오기() throws IOException {
        // given
        Path testFilePath = Files.createTempFile("testFile", ".md");
        try (BufferedWriter writer = Files.newBufferedWriter(testFilePath)) {
            writer.write("test1,test2,test3\n");
            writer.write("value1,value2,value3\n");
            writer.write("test4,test5,test7\n");
        }

        // when
        List<String[]> result = MarkdownReader.readFile(testFilePath.toString());

        // then
        assertThat(result).hasSize(2); // 첫 줄은 건너뛰므로 2줄만 읽혀야 함
        assertThat(result.get(0)).isEqualTo(new String[]{"value1", "value2", "value3"});
        assertThat(result.get(1)).isEqualTo(new String[]{"test4", "test5", "test7"});

        // 테스트 파일 삭제
        Files.deleteIfExists(testFilePath);
    }

    @Test
    void 파일이_없을_경우_예외_발생() {
        // given
        String invalidFilePath = "invalid/path/to/nonexistentFile.md";

        // then
        assertThatThrownBy(() -> MarkdownReader.readFile(invalidFilePath))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("invalid/path");
    }
}
