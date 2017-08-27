package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class Settings {

    private static Logger logger = LoggerFactory.getLogger(Settings.class);

    private Integer individualsAmount;
    private Selection selection;
    private Recombination recombination;

    public void init(String filePath) throws IOException {
        selection = new Selection();
        recombination = new Recombination();
        logger.info("Инициализация настроек. Начало");
        logger.info("Файл: " + filePath);
        List<String> content = Files.readAllLines(Paths.get(filePath));
        logger.info("Количество индивидов в популяции: " + content.get(0));
        individualsAmount = Integer.parseInt(content.get(0));
        selection.init(content.get(1).split(" "));
        logger.info("Настройки селекции: " + selection.toString());
        recombination.init(content.get(2).split(" "));
        logger.info("Настройки рекомбинации: " + recombination.toString());
        logger.info("Инициализация настроек. Окончание");
    }

    public Integer getIndividualsAmount() {
        return individualsAmount;
    }

    public void setIndividualsAmount(Integer individualsAmount) {
        this.individualsAmount = individualsAmount;
    }

    public Selection getSelection() {
        return selection;
    }

    public void setSelection(Selection selection) {
        this.selection = selection;
    }

    public Recombination getRecombination() {
        return recombination;
    }

    public void setRecombination(Recombination recombination) {
        this.recombination = recombination;
    }
}
