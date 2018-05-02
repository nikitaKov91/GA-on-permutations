package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class Problem {

    private static Logger logger = LoggerFactory.getLogger(Problem.class);

    private List<List<Integer>> coordinates;
    private Integer dimension;
    private Integer coordinatesDimension;
    private Double solution;

    public void init(String filePath) throws IOException {
        logger.info("Инициализация задачи. Начало");
        logger.info("Файл: " + filePath);
        coordinates = new ArrayList<>();
        List<String> content = Files.readAllLines(Paths.get(filePath));
        boolean isFirstLine = true;
        for (String line : content) {
            if (isFirstLine) {
                if (line != null && !"".equals(line)) {
                    solution = Double.parseDouble(line);
                    logger.info("Решение: " + solution);
                }
                isFirstLine = false;
            } else {
                List<Integer> temp = new ArrayList<>();
                for (String coord : line.split(" ")) {
                    temp.add(Integer.parseInt(coord));
                }
                coordinates.add(temp);
                if (coordinatesDimension == null) {
                    coordinatesDimension = temp.size();
                    logger.info("Размерность вектора координат: " + coordinatesDimension);
                }
            }
        }
        dimension = coordinates.size();
        logger.info("Размерность задачи: " + dimension);
        logger.info("Инициализации задачи. Окончание");
    }

    public Double calcObjectiveFunction(Individual individual) {
        logger.debug("Подсчёт целевой функции для индивида. Начало");
        logger.debug("Индивид: " + individual.toString());
        Double result = 0d;

        for (int i = 0; i < individual.getDimension() - 1; i++) {
            result += calcDistanceBetweenCities(individual.getElementByIndex(i),
                    individual.getElementByIndex(i + 1));
        }
        // расстояние между первым и последним городом тоже надо учитывать
        result += calcDistanceBetweenCities(individual.getElementByIndex(individual.getDimension() - 1),
                individual.getElementByIndex(0));
        logger.debug("Полученное значение: " + result);
        logger.debug("Подсчёт целевой функции для индивида. Окончание");
        return result;
    }

    /**
     * подсчёт расстояния между двумя городами
     * @param index0 - индекс одного города
     * @param index1 - идекс другого
     * @return - расстояние
     */
    private Double calcDistanceBetweenCities(Integer index0, Integer index1) {
        Double result = 0d;
        List<Integer> list0 = coordinates.get(index0);
        List<Integer> list1 = coordinates.get(index1);
        for (int i = 0; i < list0.size(); i++) {
            // евклидова метрика
            result += Math.pow(list0.get(i) - list1.get(i), 2);
        }
        result = Math.sqrt(result);
        return result;
    }

    public List<List<Integer>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Integer>> coordinates) {
        this.coordinates = coordinates;
    }

    public Integer getDimension() {
        return dimension;
    }

    public void setDimension(Integer dimension) {
        this.dimension = dimension;
    }

    public Integer getCoordinatesDimension() {
        return coordinatesDimension;
    }

    public void setCoordinatesDimension(Integer coordinatesDimension) {
        this.coordinatesDimension = coordinatesDimension;
    }

    public Double getSolution() {
        return solution;
    }

    public void setSolution(Double solution) {
        this.solution = solution;
    }
}
