package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.*;
import util.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class Algorithm {

    private static Logger logger = LoggerFactory.getLogger(Algorithm.class);

    private Settings settings = new Settings();
    private Problem problem = Problem.getInstance();
    private Population population = new Population();
    private Map<OperatorType, Map<OperatorSettings, Operator>> operators = new HashMap<>();

    private int countOfGenerations = 0;

    public void init(String problemFilePath, String settingsFilePath, String operatorsFolder) throws IOException {
        problem.init(problemFilePath);
        Settings.init(this, settingsFilePath);
        initOperatorSettings(operatorsFolder);
    }

    public void process() {
        logger.info("Алгоритм. Начало");
        population.init(problem.getDimension());
        do {
            for (OperatorType operatorType : OperatorType.values()) {
                population.applyOperator(operatorType, operators);
            }
            population.calcSuitability();
            population.findBest();
            population.calcOperatorFitness(operators);
            countOfGenerations += 1;
        } while (stopCriterion());
        logger.info("Алгоритм. Окончание");
    }

    private boolean stopCriterion() {
        logger.info("Алгоритм. Проверка критерия остановки. Начало");
        logger.info("Поколение " + countOfGenerations);
        boolean ret = true;
        if (countOfGenerations >= settings.getAmountOfGenerations()) {
            logger.info("Достигнут максимум итераций");
            ret = false;
        }
        if (problem.getSolution() != null) {
            if (population.getBestIndividual().getObjectiveFunctionValue() <= problem.getSolution() + settings.getAccuracy()) {
                logger.info("Достигнут ответ с заданной точностью");
                ret = false;
            }
        }
        if (ret) {
            logger.info("Ни один критерий остановки не сработал");
        }

        logger.info("Алгоритм. Проверка критерия остановки. Окончание");
        return ret;
    }

    private void initOperatorSettings(String operatorsFolder) throws IOException {
        logger.info("Алгоритм. Инициализация настроек операторов. Начало");

        for (OperatorType operatorType : OperatorType.values()) {
            operators.put(operatorType, new HashMap<>());
        }

        int operatorsCount = 0;
        File operatorFile = new File(operatorsFolder + "\\operator" + operatorsCount + ".txt");
        while (operatorFile.exists()) {
            List<String> content = Files.readAllLines(Paths.get(operatorFile.getAbsolutePath()));
            Operator operator = OperatorFactory.createOperator(content);
            OperatorSettings operatorSettings = operator.getSettings();
            operators.get(operatorSettings.getOperatorType()).put(operatorSettings, operator);
            operatorsCount++;
            operatorFile = new File(operatorsFolder + "\\operator" + operatorsCount + ".txt");
        }

        for (Map.Entry<OperatorType, Map<OperatorSettings, Operator>> entry : operators.entrySet()) {
            Operator.setOperatorsInitialProbabilities(entry.getValue());
            Operator.setOperatorsDistribution(entry.getValue());
        }

        logger.info("Алгоритм. Инициализация настроек операторов. Окончание");
    }

    public Problem getProblem() {
        return problem;
    }

    public Population getPopulation() {
        return population;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
