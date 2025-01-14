package refactor.concurrent;

import refactor.LanguageModel;
import refactor.TextProcessor;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrentProcessor {
  private final TextProcessor textprocessor;
  private final int threadPoolSize;
//  private final ExecutorService executor;


  public ConcurrentProcessor(TextProcessor textProcessor) {
    this.textprocessor = textProcessor;
    this.threadPoolSize = Runtime.getRuntime().availableProcessors();
  }

  public void processLanguageFolder(Path folderPath, LanguageModel model) {
    ExecutorService executor = createThreadPool();
    List<Path> textFiles = textprocessor.getTextFilePaths(folderPath);

    textFiles.forEach(file ->
        executor.submit(() -> processFile(file, model))
    );

    shutdownAndAwaitTermination(executor);
  }

  private void processFile(Path filePath, LanguageModel model) {
    List<String> lines = textprocessor.readFile(filePath);
    String content = String.join(" ", lines);
    String processedText = textprocessor.preprocessText(content);
    model.processText(processedText);
  }

  private ExecutorService createThreadPool() {
    return Executors.newFixedThreadPool(threadPoolSize);
  }


  private void shutdownAndAwaitTermination(ExecutorService executor) {
    try {
      executor.shutdown();
      if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
        executor.shutdownNow();
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
          System.err.println("Pool did not terminate");
        }
      }
    } catch (InterruptedException ie) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

//  public void processLanguageFolder(Path folderPath) {
//
//  }
//
//  public void processFile(Path filePath, LanguageModel model) {
//
//  }
//
//  public ExecutorService threadPool() {
//    return null;
//  }

}
