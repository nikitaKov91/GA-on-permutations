package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.OperatorSettings;
import util.OperatorFactory;
import util.OperatorType;
import util.RandomUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.OperatorType.*;
import static util.RandomUtils.getRandomIndexExclude;

/**
 * Created by Коваленко Никита on 10.01.2018.
 */
public abstract class Operator {

    public static final double MIN_OPERATOR_PROBABILITY = 0.02d;

    private static Logger logger = LoggerFactory.getLogger(Operator.class);

    private Double probability;
    private Double distribution;
    private Double fitness;
    private int amountOfIndividuals;

    private Map<OperatorType, Map<OperatorSettings, Operator>> relatedOperators = new HashMap<>();

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public Double getDistribution() {
        return distribution;
    }

    public void setDistribution(Double distribution) {
        this.distribution = distribution;
    }

    public abstract OperatorSettings getSettings();

    public Map<OperatorType, Map<OperatorSettings, Operator>> getRelatedOperators() {
        return relatedOperators;
    }

    public abstract void apply(Individual individual);

    public abstract void apply(List<Individual> individuals,  List<Individual> parents, int param);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operator operator = (Operator) o;

        return getSettings().equals(operator.getSettings());
    }

    public static void setOperatorsInitialProbabilities(Map<OperatorSettings, Operator> operators) {
        logger.debug("Задание изначальных вероятностей операторов. Начало");
        double probability = 1.0/operators.size();
        logger.debug("Вероятность: " + probability);
        for (Map.Entry<OperatorSettings, Operator> entry : operators.entrySet()) {
            entry.getValue().setProbability(probability);
        }
        logger.debug("Задание изначальных вероятностей операторов. Окончание");
    }

    public static void setOperatorsProbabilities(Map<OperatorSettings, Operator> operators, int generationsAmount) {
        logger.debug("Задание вероятностей операторов. Начало");
        double maxFitness = 0;
        List<Operator> bestOperators = new ArrayList<>();
        for (Map.Entry<OperatorSettings, Operator> entry : operators.entrySet()) {
            Operator operator = entry.getValue();
            if (operator.fitness > maxFitness) {
                maxFitness = operator.fitness;
                bestOperators.clear();
                bestOperators.add(operator);
            } else if (operator.fitness == maxFitness) {
                bestOperators.add(operator);
            }
        }
        Operator bestOperator;
        if (bestOperators.size() == 1) {
            bestOperator = bestOperators.get(0);
        } else {
            int index = getRandomIndexExclude(null, bestOperators.size());
            bestOperator = bestOperators.get(index);
        }

        logger.debug("Лучший оператор: " + bestOperator);

        // вероятности всех остальных операторов уменьшаются на K /(z * N)),
        // где N – число поколений, K – константа, обычно равная 2
        double reduceProbability = 2d / (operators.size() * generationsAmount);
        double increaseProbability = 0;
        for (Map.Entry<OperatorSettings, Operator> entry : operators.entrySet()) {
            Operator operator = entry.getValue();
            if (!operator.equals(bestOperator)) {
                // необходимо установить порог вероятности т.к. ни у одного варианта оператора не может быть нулевой вероятности
                // при достижении минимально возможного значения вероятности вариант оператора перестает отдавать свою часть в пользу лучшего
                if (operator.probability - reduceProbability <= MIN_OPERATOR_PROBABILITY) {
                    increaseProbability += operator.probability - MIN_OPERATOR_PROBABILITY;
                    operator.probability = MIN_OPERATOR_PROBABILITY;
                } else {
                    increaseProbability += reduceProbability;
                    operator.probability -= reduceProbability;
                }
            }
        }
        bestOperator.probability += increaseProbability;
        logger.debug("Задание вероятностей операторов. Окончание");
    }

    public static void setOperatorsDistribution(Map<OperatorSettings, Operator> operators) {
        logger.debug("Задание распределения операторов. Начало");
        Double distribution = 0.0;
        for (Map.Entry<OperatorSettings, Operator> entry : operators.entrySet()) {
            distribution += entry.getValue().getProbability();
            entry.getValue().setDistribution(distribution);
        }
        logger.debug("Задание распределения операторов. Окончание");
    }

    public static Operator selectOperator(Map<OperatorSettings, Operator> operators) {
        logger.debug("Выбор оператора. Начало");
        Operator result = null;

        if (operators.size() == 1) {
            result = operators.entrySet().iterator().next().getValue();
        } else {
            double random = RandomUtils.random.nextDouble();
            for (Map.Entry<OperatorSettings, Operator> entry : operators.entrySet()) {
                if (entry.getValue().getDistribution() >= random) {
                    result = entry.getValue();
                    break;
                }
            }
        }

        logger.debug("Выбранный оператор: " + result);
        logger.debug("Выбор оператора. Окончание");
        return result;
    }

    public static void calcOperatorFitness(List<Individual> individuals, Map<OperatorType, Map<OperatorSettings, Operator>> operators) {
        // обнуляем пригодность и кол-во индивидов
        for (Map.Entry<OperatorType, Map<OperatorSettings, Operator>> entry : operators.entrySet()) {
            for (Map.Entry<OperatorSettings, Operator> operator : entry.getValue().entrySet()) {
                operator.getValue().fitness = 0.0;
                operator.getValue().amountOfIndividuals = 0;
            }
        }
        // считаем кол-во индивидов по определённому оператору и сумму их пригодностей
        for (Individual individual : individuals) {
            for (Map.Entry<OperatorType, Map<OperatorSettings, Operator>> entry : operators.entrySet()) {
                // в общей мапе ключ - тип оператора, в мапе индвида - такой же
                OperatorSettings operatorSettings = individual.getOperatorsSettings().get(entry.getKey());
                Operator operator = entry.getValue().get(operatorSettings);
                operator.amountOfIndividuals += 1;
                operator.fitness += individual.getFitness();
            }
        }
        // считаем среднюю пригодность
        for (Map.Entry<OperatorType, Map<OperatorSettings, Operator>> entry : operators.entrySet()) {
            for (Map.Entry<OperatorSettings, Operator> operator : entry.getValue().entrySet()) {
                int amountOfIndividuals = operator.getValue().amountOfIndividuals;
                if (amountOfIndividuals != 0) {
                    operator.getValue().fitness /= amountOfIndividuals;
                }
            }
        }
        // а теперь для связанных операторов
        for (Map.Entry<OperatorType, Map<OperatorSettings, Operator>> entry : operators.entrySet()) {
            for (Map.Entry<OperatorSettings, Operator> operator : entry.getValue().entrySet()) {
                Map<OperatorType, Map<OperatorSettings, Operator>> relatedOperators = operator.getValue().getRelatedOperators();
                if (relatedOperators.size() > 0) {
                    calcOperatorFitness(individuals, relatedOperators);
                }
            }
        }
    }

    public static void initOperatorSettings(Map<OperatorType, Map<OperatorSettings, Operator>> operators,
                                            String operatorsFolder) throws IOException {
        logger.info("Инициализация настроек операторов. Начало");

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

        logger.info("Инициализация настроек операторов. Окончание");
    }

    public static List<List<String>> getOperatorsSettings(OperatorType needOperatorType, String operatorsFolder) throws IOException {
        List<List<String>> result = new ArrayList<>();

        int operatorsCount = 0;
        File operatorFile = new File(operatorsFolder + "\\operator" + operatorsCount + ".txt");
        while (operatorFile.exists()) {
            List<String> content = Files.readAllLines(Paths.get(operatorFile.getAbsolutePath()));
            OperatorType operatorType = OperatorType.valueOf(content.get(0));
            if (operatorType == needOperatorType) {
                result.add(content);
            }
            operatorsCount++;
            operatorFile = new File(operatorsFolder + "\\operator" + operatorsCount + ".txt");
        }

        return result;
    }

    public static void writeHeadProbabilitiesInFiles(Map<OperatorType, Map<OperatorSettings, Operator>> operators, int problemNumber) throws IOException {
        writeHeadProbabilitiesInFiles(operators, problemNumber, null);
    }

    public static void writeHeadProbabilitiesInFiles(Map<OperatorType, Map<OperatorSettings, Operator>> operators,
                                                     int problemNumber, OperatorType relatedOperatorType) throws IOException {
        StringBuilder sbRecombinations = new StringBuilder();
        StringBuilder sbSelections = new StringBuilder();
        StringBuilder sbMutations = new StringBuilder();

        for (Map.Entry<OperatorType, Map<OperatorSettings, Operator>> entry : operators.entrySet()) {
            for (Map.Entry<OperatorSettings, Operator> operator : entry.getValue().entrySet()) {
                OperatorType operatorType = operator.getKey().getOperatorType();
                if (operatorType != relatedOperatorType) {
                    StringBuilder sb = null;
                    if (operatorType == RECOMBINATION) {
                        sb = sbRecombinations;
                    } else if (operatorType == SELECTION) {
                        sb = sbSelections;
                    } else if (operatorType == MUTATION) {
                        sb = sbMutations;
                    }
                    if (sb.length() > 0) {
                        sb.append("\t");
                    }
                    Map<OperatorType, Map<OperatorSettings, Operator>> relatedOperators = operator.getValue().getRelatedOperators();
                    if (relatedOperators.size() == 0) {
                        sb.append(operator.getKey().toString());
                    } else {
                        for (Map.Entry<OperatorType, Map<OperatorSettings, Operator>> relEntry : relatedOperators.entrySet()) {
                            for (Map.Entry<OperatorSettings, Operator> relatedOperator : relEntry.getValue().entrySet()) {
                                sb.append(operator.getKey().toString()).append(" ").
                                        append(relatedOperator.getKey().toString()).append("\t");
                            }
                        }
                    }
                }
            }
        }

        List<String> recombinations = new ArrayList<>();
        List<String> selections = new ArrayList<>();
        List<String> mutations = new ArrayList<>();
        recombinations.add(sbRecombinations.toString());
        selections.add(sbSelections.toString());
        mutations.add(sbMutations.toString());

        Files.write(Paths.get("probabilities\\problem" + problemNumber + "-recombinations.txt"), recombinations);
        Files.write(Paths.get("probabilities\\problem" + problemNumber + "-selections.txt"), selections);
        Files.write(Paths.get("probabilities\\problem" + problemNumber + "-mutations.txt"), mutations);
    }

    public static void writeProbabilitiesInFiles(Map<OperatorType, Map<OperatorSettings, Operator>> operators,
                                                 int countOfGenerations, int problemNumber) throws IOException {
        writeProbabilitiesInFiles(operators, countOfGenerations, problemNumber, null);
    }

    public static void writeProbabilitiesInFiles(Map<OperatorType, Map<OperatorSettings, Operator>> operators,
                                                 int countOfGenerations, int problemNumber,
                                                 OperatorType relatedOperatorType) throws IOException {

        StringBuilder sbRecombinations = new StringBuilder();
        StringBuilder sbSelections = new StringBuilder();
        StringBuilder sbMutations = new StringBuilder();

        if (relatedOperatorType != RECOMBINATION) sbRecombinations.append(countOfGenerations + "\t");
        if (relatedOperatorType != SELECTION) sbSelections.append(countOfGenerations + "\t");
        if (relatedOperatorType != MUTATION) sbMutations.append(countOfGenerations + "\t");

        for (Map.Entry<OperatorType, Map<OperatorSettings, Operator>> entry : operators.entrySet()) {
            for (Map.Entry<OperatorSettings, Operator> operator : entry.getValue().entrySet()) {
                OperatorType operatorType = operator.getKey().getOperatorType();
                if (operatorType != relatedOperatorType) {
                    StringBuilder sb = null;
                    if (operatorType == RECOMBINATION) {
                        sb = sbRecombinations;
                    } else if (operatorType == SELECTION) {
                        sb = sbSelections;
                    } else if (operatorType == MUTATION) {
                        sb = sbMutations;
                    }
                    if (sb.length() > 0) {
                        sb.append("\t");
                    }
                    Map<OperatorType, Map<OperatorSettings, Operator>> relatedOperators = operator.getValue().getRelatedOperators();
                    if (relatedOperators.size() == 0) {
                        sb.append(operator.getValue().probability);
                    } else {
                        for (Map.Entry<OperatorType, Map<OperatorSettings, Operator>> relEntry : relatedOperators.entrySet()) {
                            for (Map.Entry<OperatorSettings, Operator> relatedOperator : relEntry.getValue().entrySet()) {
                                sb.append(operator.getValue().probability * relatedOperator.getValue().probability).append("\t");
                            }
                        }
                    }
                }
            }
        }

        List<String> recombinations = new ArrayList<>();
        List<String> selections = new ArrayList<>();
        List<String> mutations = new ArrayList<>();
        recombinations.add(sbRecombinations.toString());
        selections.add(sbSelections.toString());
        mutations.add(sbMutations.toString());

        Files.write(Paths.get("probabilities\\problem" + problemNumber + "-recombinations.txt"),
                recombinations, StandardOpenOption.APPEND);
        Files.write(Paths.get("probabilities\\problem" + problemNumber + "-selections.txt"), selections,
                StandardOpenOption.APPEND);
        Files.write(Paths.get("probabilities\\problem" + problemNumber + "-mutations.txt"), mutations,
                StandardOpenOption.APPEND);
    }

}
