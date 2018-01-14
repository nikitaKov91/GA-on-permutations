package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.MutationSettings;
import settings.OperatorSettings;
import settings.RecombinationSettings;
import settings.SelectionSettings;
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

    public static void calcOperatorFitness(List<Individual> individuals, Map<OperatorSettings, Operator> ... operators) {
        // для начала определяем индекс каждого типа операторов
        int selectionIndex = 0;
        int recombinationIndex = 1;
        int mutationIndex = 2;
        for (int i = 0; i < operators.length; i++) {
            OperatorSettings operatorSettings = operators[i].keySet().iterator().next();
            if (operatorSettings.getOperatorType() == OperatorType.SELECTION) {
                selectionIndex = i;
            } else if (operatorSettings.getOperatorType() == OperatorType.RECOMBINATION) {
                recombinationIndex = i;
            } else if (operatorSettings.getOperatorType() == OperatorType.MUTATION) {
                mutationIndex = i;
            }
        }
        // обнуляем пригодность и кол-во индивидов
        for (int i = 0; i < operators.length; i++) {
            for (Map.Entry<OperatorSettings, Operator> entry : operators[i].entrySet()) {
                entry.getValue().fitness = 0.0;
                entry.getValue().amountOfIndividuals = 0;
            }
        }
        // считаем кол-во индивидов по определённому оператору и сумму их пригодностей
        for (Individual individual : individuals) {
            for (int i = 0; i < operators.length; i++) {
                Operator operator = null;
                if (i == selectionIndex) {
                    operator = operators[i].get(individual.getOperatorsSettings().get(OperatorType.SELECTION));
                } else if (i == recombinationIndex) {
                    operator = operators[i].get(individual.getOperatorsSettings().get(OperatorType.RECOMBINATION));
                } else if (i == mutationIndex) {
                    operator = operators[i].get(individual.getOperatorsSettings().get(OperatorType.MUTATION));
                }
                operator.amountOfIndividuals += 1;
                operator.fitness += individual.getSuitability();
            }
        }
        // считаем среднюю пригодность
        for (int i = 0; i < operators.length; i++) {
            for (Map.Entry<OperatorSettings, Operator> entry : operators[i].entrySet()) {
                entry.getValue().fitness /= entry.getValue().amountOfIndividuals;
            }
        }
    }

}
