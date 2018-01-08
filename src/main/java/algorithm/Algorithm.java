package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.Settings;

import java.io.IOException;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class Algorithm {

    private static Logger logger = LoggerFactory.getLogger(Algorithm.class);

    private Settings settings = new Settings();
    private Problem problem = Problem.getInstance();
    private Selection selection = new Selection();
    private Recombination recombination = new Recombination();
    private Mutation mutation = new Mutation();
    private Population population = new Population();

    private int countOfGenerations = 0;

    public void init(String problemFilePath, String settingsFilePath) throws IOException {
        problem.init(problemFilePath);
        Settings.init(this, settingsFilePath);
    }

    public void process() {
        logger.info("Алгоритм. Начало");
        population.init(problem.getDimension());
        do {
            population.selection(selection);
            population.recombination(recombination);
            population.mutation(mutation);
            population.calcSuitability();
            population.findBest();
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
        if (population.getBestIndividual().getObjectiveFunctionValue() <= problem.getSolution() + settings.getAccuracy()) {
            logger.info("Достигнут ответ с заданной точностью");
            ret = false;
        }
        if (ret) {
            logger.info("Ни один критерий остановки не сработал");
        }

        logger.info("Алгоритм. Проверка критерия остановки. Окончание");
        return ret;
    }

    public Problem getProblem() {
        return problem;
    }

    public Selection getSelection() {
        return selection;
    }

    public Recombination getRecombination() {
        return recombination;
    }

    public Mutation getMutation() {
        return mutation;
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
