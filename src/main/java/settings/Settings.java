package settings;

import algorithm.Algorithm;
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

    private int generationsAmount;
    private Double accuracy;

    public static void init(Algorithm algorithm, String filePath) throws IOException {
        logger.info("Инициализация настроек. Начало");
        logger.info("Файл: " + filePath);
        List<String> content = Files.readAllLines(Paths.get(filePath));
        algorithm.getPopulation().setIndividualsAmount(Integer.parseInt(content.get(0)));
        logger.info("Количество индивидов в популяции: " + content.get(0));
        algorithm.getSettings().generationsAmount = Integer.parseInt(content.get(1));
        logger.info("Максимальное количество итераций: " + content.get(1));
        algorithm.getSettings().accuracy = Double.parseDouble(content.get(2));
        logger.info("Точность: " + content.get(2));
        logger.info("Инициализация настроек. Окончание");
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public int getGenerationsAmount() {
        return generationsAmount;
    }
}
