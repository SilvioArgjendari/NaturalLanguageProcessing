package refactor;

import java.nio.file.Path;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    /**
     * TODO
     * Read the files
     * Determine the languages and build the models
     * Construct the histogram for each model
     *
     */

    TextProcessor processor = new TextProcessor();

    List<Path> list = processor.getLanguageFolders(Path.of("languages"));

    String text= "Në këtë punim shqyrtohet saktësia e disa metodave standarde dhe bashkëkohore në identifikimin";

    LanguageModel model = new LanguageModel(3);
    List<String> nGramList = model.generateNGrams(text);


  }
}
