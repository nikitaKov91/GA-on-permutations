package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.*;
import util.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class Algorithm {

    private static Logger logger = LoggerFactory.getLogger(Algorithm.class);

    private Settings settings = new Settings();
    private Problem problem = Problem.getInstance();
    private Population population = new Population();

    private Map<OperatorSettings, Operator> selections;
    private Map<OperatorSettings, Operator> recombinations;
    private Map<OperatorSettings, Operator> mutations;

    private int countOfGenerations = 0;

    public void init(String problemFilePath, String settingsFilePath) throws IOException {
        problem.init(problemFilePath);
        Settings.init(this, settingsFilePath);
    }

    public void process() {
        logger.info("Алгоритм. Начало");
        initOperatorSettings();
        population.init(problem.getDimension());
        do {
            population.selection(selections);
            population.recombination(recombinations);
            population.mutation(mutations);
            population.calcSuitability();
            population.findBest();
            population.calcOperatorFitness(selections, recombinations, mutations);
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

    private void initRecombinationSettings() {
        recombinations = new HashMap<>(1);
        RecombinationSettings recombinationSettings = RecombinationSettings.create()
                .recombinationType(RecombinationType.TYPICAL);
        recombinations.put(recombinationSettings, new Recombination(recombinationSettings));
    }

    private void initSelectionSettings() {
        selections = new HashMap<>(8);
        SelectionSettings selectionSettings0 = SelectionSettings.create()
                .selectionType(SelectionType.PROPORTIONAL);
        selections.put(selectionSettings0, new Selection(selectionSettings0));

        SelectionSettings selectionSettings1 = SelectionSettings.create()
                .selectionType(SelectionType.TOURNAMENT)
                .tournamentSize(2);
        selections.put(selectionSettings1, new Selection(selectionSettings1));
        SelectionSettings selectionSettings2 = SelectionSettings.create()
                .selectionType(SelectionType.TOURNAMENT)
                .tournamentSize(4);
        selections.put(selectionSettings2, new Selection(selectionSettings2));
        SelectionSettings selectionSettings3 = SelectionSettings.create()
                .selectionType(SelectionType.TOURNAMENT)
                .tournamentSize(8);
        selections.put(selectionSettings3, new Selection(selectionSettings3));

        SelectionSettings selectionSettings4 = SelectionSettings.create()
                .selectionType(SelectionType.RANKING)
                .rankingSelectionType(RankingSelectionType.LINEAR);
        selections.put(selectionSettings4, new Selection(selectionSettings4));
        SelectionSettings selectionSettings5 = SelectionSettings.create()
                .selectionType(SelectionType.RANKING)
                .rankingSelectionType(RankingSelectionType.EXPONENTIAL)
                .weight(0.95);
        selections.put(selectionSettings5, new Selection(selectionSettings5));
        SelectionSettings selectionSettings6 = SelectionSettings.create()
                .selectionType(SelectionType.RANKING)
                .rankingSelectionType(RankingSelectionType.EXPONENTIAL)
                .weight(0.8);
        selections.put(selectionSettings6, new Selection(selectionSettings6));
        SelectionSettings selectionSettings7 = SelectionSettings.create()
                .selectionType(SelectionType.RANKING)
                .rankingSelectionType(RankingSelectionType.EXPONENTIAL)
                .weight(0.5);
        selections.put(selectionSettings7, new Selection(selectionSettings7));
    }

    private void initMutationSettings() {
        mutations = new HashMap<>(20);
        for (MutationType mutationType : MutationType.values()) {
            for (MutationProbabilityType mutationProbabilityType : MutationProbabilityType.values()) {
                MutationSettings mutationSettings = MutationSettings.create()
                        .mutationType(mutationType)
                        .mutationProbabilityType(mutationProbabilityType);
                mutations.put(mutationSettings, new Mutation(mutationSettings));
            }
        }
    }

    private void initOperatorSettings() {
        logger.info("Алгоритм. Инициализация настроек операторов. Начало");

        // рекомбинации
        initRecombinationSettings();
        Operator.setOperatorsInitialProbabilities(recombinations);
        Operator.setOperatorsDistribution(recombinations);

        // селекции
        initSelectionSettings();
        Operator.setOperatorsInitialProbabilities(selections);
        Operator.setOperatorsDistribution(selections);

        // мутации
        initMutationSettings();
        Operator.setOperatorsInitialProbabilities(mutations);
        Operator.setOperatorsDistribution(mutations);

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
