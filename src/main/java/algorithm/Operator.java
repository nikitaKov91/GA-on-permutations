package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.OperatorSettings;
import util.OperatorType;
import util.RandomUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Коваленко Никита on 10.01.2018.
 */
public abstract class Operator {

    private static Logger logger = LoggerFactory.getLogger(Operator.class);

    private Double probability;
    private Double distribution;
    private Double fitness;
    private int amountOfIndividuals;

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

    public Double getFitness() {
        return fitness;
    }

    public void setFitness(Double fitness) {
        this.fitness = fitness;
    }

    public int getAmountOfIndividuals() {
        return amountOfIndividuals;
    }

    public void setAmountOfIndividuals(int amountOfIndividuals) {
        this.amountOfIndividuals = amountOfIndividuals;
    }

    public abstract OperatorSettings getSettings();

    public static void setOperatorsInitialProbabilities(Map<OperatorSettings, Operator> operators) {
        logger.debug("Задание изначальных вероятностей операторов. Начало");
        double probability = 1.0/operators.size();
        logger.debug("Вероятность: " + probability);
        for (Map.Entry<OperatorSettings, Operator> entry : operators.entrySet()) {
            entry.getValue().setProbability(probability);
        }
        logger.debug("Задание изначальных вероятностей операторов. Окончание");
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
                if (entry.getValue().getDistribution() > random) {
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
                operator.fitness += individual.getSuitability();
            }
        }
        // считаем среднюю пригодность
        for (Map.Entry<OperatorType, Map<OperatorSettings, Operator>> entry : operators.entrySet()) {
            for (Map.Entry<OperatorSettings, Operator> operator : entry.getValue().entrySet()) {
                operator.getValue().fitness /= operator.getValue().amountOfIndividuals;
            }
        }
    }

}
