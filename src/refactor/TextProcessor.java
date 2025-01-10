package refactor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextProcessor {

  /**
   * Reads the entire file content into a string.
   * @param filePath absolute path
   * @return File content
   */
  public List<String> readFile(Path filePath) {
    try (Stream<String> lines = Files.lines(filePath)) {
      return lines.collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException("Error reading file: " + filePath, e);
    }
  }

  /**
   * Standardizes text
   */
  public String preprocessText(String text) {
    return text.toLowerCase()
        .replaceAll("[^a-z\\s]", " ")  // Replace punctuation with whitespace
        .replaceAll("\\s+", " ")       // Normalize whitespace
        .trim();
  }

  /**
   * Returns a list of paths for all .txt files in a folder.
   * @param folderPath
   * @return List of Paths of all .txt files in the folder
   */
  public List<Path> getTextFiles(Path folderPath) {
    try (Stream<Path> files = Files.walk(folderPath)) {
      return files
          .filter(Files::isRegularFile)
          .filter(file -> file.getFileName().toString().endsWith(".txt"))
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException("Error listing text files from: " + folderPath, e);
    }
  }

  /**
   * Gets the folder path for each language folder.
   * @param rootPath
   * @return
   */
  public List<Path> getLanguageFolders(Path rootPath) {
    try (Stream<Path> folders = Files.list(rootPath)) {
      return folders
          .filter(Files::isDirectory)
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException("Error getting language folders from: " + rootPath, e);
    }
  }
}
