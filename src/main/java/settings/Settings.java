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

    private int amountOfGenerations;
    private Double accuracy;
    private Double solution;

    public static void init(Algorithm algorithm, String filePath) throws IOException {
        logger.info("Инициализация настроек. Начало");
        logger.info("Файл: " + filePath);
        List<String> content = Files.readAllLines(Paths.get(filePath));
        algorithm.getPopulation().setIndividualsAmount(Integer.parseInt(content.get(0)));
        logger.info("Количество индивидов в популяции: " + content.get(0));
        algorithm.getSelection().init(content.get(1).split(" "));
        logger.info("Настройки селекции: " + algorithm.getSelection().getSettings().toString());
        SelectionSettings selectionSettings = algorithm.getSelection().getSettings();
        if (selectionSettings.getSelectionType().equals(util.SelectionType.TOURNAMENT)) {
            if (selectionSettings.getTournamentSize() > algorithm.getPopulation().getIndividualsAmount()) {
                throw new IllegalArgumentException("Размер турнира больше количества индивидов," +
                        " пожалуйста, измените настройки");
            }
        }
        algorithm.getRecombination().init(content.get(2).split(" "));
        logger.info("Настройки рекомбинации: " + algorithm.getRecombination().getSettings().toString());
        algorithm.getMutation().init(content.get(3).split(" "), algorithm.getProblem().getDimension());
        logger.info("Настройки мутации: " + algorithm.getMutation().getSettings().toString());
        algorithm.getSettings().setAmountOfGenerations(Integer.parseInt(content.get(4)));
        logger.info("Максимальное количество итераций: " + content.get(4));
        algorithm.getSettings().setAccuracy(Double.parseDouble(content.get(5)));
        logger.info("Точность: " + content.get(5));
        if (content.size() > 5) {
            String solution = content.get(6);
            if (solution != null && !solution.equals("")) {
                algorithm.getSettings().setSolution(Double.parseDouble(solution));
                logger.info("Решение: " + content.get(6));
            }
        }
        logger.info("Инициализация настроек. Окончание");
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public Double getSolution() {
        return solution;
    }

    public void setSolution(Double solution) {
        this.solution = solution;
    }

    public int getAmountOfGenerations() {
        return amountOfGenerations;
    }

    public void setAmountOfGenerations(int amountOfGenerations) {
        this.amountOfGenerations = amountOfGenerations;
    }

}
