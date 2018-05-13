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
    protected static final int AMOUNT_OF_ITERATIONS_FOR_PROBABILITIES_OUTPUT = 10;
    protected static final int REPLACEMENT_PERCENT = 1;

    protected Settings settings = new Settings();
    protected Problem problem = new Problem();
    protected Population population = new Population();
    protected Map<OperatorType, Map<OperatorSettings, Operator>> operators = new HashMap<>();

    protected int countOfGenerations = 0;

    public void init(String problemFilePath, String settingsFilePath) throws IOException {
        problem.init(problemFilePath);
        Settings.init(this, settingsFilePath);
    }

    public void process(int problemNumber, boolean isWriteProbabilitiesInFiles) throws IOException {
        logger.info("Алгоритм. Начало");
        population.init(problem);
        if (isWriteProbabilitiesInFiles) {
            Operator.writeHeadProbabilitiesInFiles(operators, problemNumber);
        }
        do {
            countOfGenerations += 1;
            for (OperatorType operatorType : OperatorType.values()) {
                population.applyOperator(operatorType, operators);
            }
            population.calcFitness(problem);
            population.findBest();
            population.calcOperatorsFitness(operators);
            population.configureOperators(operators, settings.getGenerationsAmount());
            if (isWriteProbabilitiesInFiles) {
                if (countOfGenerations % AMOUNT_OF_ITERATIONS_FOR_PROBABILITIES_OUTPUT == 0) {
                    Operator.writeProbabilitiesInFiles(operators, countOfGenerations, problemNumber);
                }
            }
        } while (stopCriterion());
        logger.info("Алгоритм. Окончание");
    }

    public void process(int problemNumber, boolean isWriteProbabilitiesInFiles, boolean isUseElitism) throws IOException {
        logger.info("Алгоритм. Начало");
        Integer amountOfReplace = (int) Math.ceil(problem.getDimension() * REPLACEMENT_PERCENT / 100d);
        population.init(problem);
        if (isWriteProbabilitiesInFiles) {
            Operator.writeHeadProbabilitiesInFiles(operators, problemNumber);
        }
        do {
            countOfGenerations += 1;
            for (OperatorType operatorType : OperatorType.values()) {
                population.applyOperator(operatorType, operators);
            }
            population.calcFitness(problem);
            population.replacement();
            population.findBest();
            population.calcOperatorsFitness(operators);
            population.configureOperators(operators, settings.getGenerationsAmount());
            if (isWriteProbabilitiesInFiles) {
                if (countOfGenerations % AMOUNT_OF_ITERATIONS_FOR_PROBABILITIES_OUTPUT == 0) {
                    Operator.writeProbabilitiesInFiles(operators, countOfGenerations, problemNumber);
                }
            }
            population.rememberGeneration(amountOfReplace);
        } while (stopCriterion());
        logger.info("Алгоритм. Окончание");
    }

    protected boolean stopCriterion() {
        logger.info("Алгоритм. Проверка критерия остановки. Начало");
        logger.info("Поколение " + countOfGenerations);
        boolean ret = true;
        if (countOfGenerations >= settings.getGenerationsAmount()) {
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

    public void initOperatorSettings(String operatorsFolder) throws IOException {
        Operator.initOperatorSettings(operators, operatorsFolder);
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

    public void setOperators(Map<OperatorType, Map<OperatorSettings, Operator>> operators) {
        this.operators = operators;
    }
}
