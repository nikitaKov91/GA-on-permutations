package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.OperatorSettings;
import util.IndividualComparator;
import util.OperatorType;

import java.util.*;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class Population {

    private static Logger logger = LoggerFactory.getLogger(Population.class);

    private List<Individual> parents = new ArrayList<>();
    private List<Individual> oldGeneration;
    private List<Individual> newGeneration;
    private Individual bestIndividual;
    private Integer individualsAmount;

    public void init(Problem problem) {
        logger.info("Ининициализация популяции. Начало");
        newGeneration = new ArrayList<>();
        oldGeneration = new ArrayList<>();
        for (int i = 0; i < individualsAmount; i++) {
            newGeneration.add(Individual.createIndividual(problem.getDimension()));
        }
        calcFitness(problem);
        findBest();
        logger.info("Ининициализация популяции. Окончание");
    }

    public void calcFitness(Problem problem) {
        logger.info("Подсчёт значения функции пригодности в популяции. Начало");
        for (Individual individual : newGeneration) {
            individual.calcFitness(problem);
        }
        logger.info("Подсчёт значения функции пригодности в популяции. Окончание");
    }

    public Individual getBest(List<Individual> individuals) {
        int i = 0;
        int index = 0;
        Double bestFitness = 0d;
        for (Individual individual : individuals) {
            if (individual.getFitness() > bestFitness) {
                bestFitness = individual.getFitness();
                index = i;
            }
            i++;
        }
        return individuals.get(index);
    }

    public void findBest() {
        logger.info("Выбор лучшего индивида в популяции. Начало");
        Individual best = getBest(newGeneration);
        // первое поколение - просто берём лучшего
        if (bestIndividual == null) {
            bestIndividual = Individual.clone(best);
        } else {
            // для всех остальных сравниваем пригодность
            if (bestIndividual.getFitness() < best.getFitness()) {
                logger.debug("Заменяем предыдущего лучшего индивида: " + bestIndividual.toString());
                logger.debug("Пригодность данного индивида: " + bestIndividual.getFitness());
                logger.debug("Значение оптимизируемой функции для данного индивида: " + bestIndividual.getObjectiveFunctionValue());
                bestIndividual = Individual.clone(best);
            } else {
                logger.debug("Пригодность лучшего индивида в популяции: " + best.getFitness());
                logger.debug("Значение оптимизируемой функции для данного индивида: " + best.getObjectiveFunctionValue());
                logger.debug("Сам индивид: " + best.toString());
                logger.debug("Он хуже предыдущего, замену не производим");
            }
        }

        logger.info("Лучший индивид: " + bestIndividual.toString());
        logger.info("Пригодность данного индивида: " + bestIndividual.getFitness());
        logger.info("Значение оптимизируемой функции для данного индивида: " + bestIndividual.getObjectiveFunctionValue());
        logger.info("Выбор лучшего индивида в популяции. Окончание");
    }

    public void rememberGeneration(int amount) {
        Collections.sort(newGeneration, new IndividualComparator());
        oldGeneration.clear();
        // округляем в большую сторону
        int size = newGeneration.size();
        // забираем индивидов с лучшей пригодностью
        while (amount > 0) {
            oldGeneration.add(Individual.clone(newGeneration.get(size - amount)));
            amount -= 1;
        }
    }

    public void replacement() {
        if (oldGeneration.size() > 0) {
            Collections.sort(newGeneration, new IndividualComparator());
            newGeneration.subList(0, oldGeneration.size()).clear();
            for (Individual individual : oldGeneration) {
                newGeneration.add(Individual.clone(individual));
            }
        }
    }

    public void applyOperator(OperatorType operatorType, Map<OperatorType, Map<OperatorSettings, Operator>> operators) {
        switch (operatorType) {
            case SELECTION:
                logger.info("Селекция в популяции. Начало");
                parents.clear();
                for (int i = 0; i < individualsAmount; i++) {
                    Operator operator = Operator.selectOperator(operators.get(operatorType));
                    operator.apply(newGeneration, parents, 2);
                }
                logger.info("Селекция в популяции. Окончание");
                break;
            case RECOMBINATION:
                logger.info("Рекомбинация в популяции. Начало");
                newGeneration.clear();
                for (int i = 0; i < parents.size(); i += 2) {
                    Operator operator = Operator.selectOperator(operators.get(operatorType));
                    operator.apply(newGeneration, parents, i);
                }
                logger.info("Рекомбинация в популяции. Окончание");
                break;
            case MUTATION:
                logger.info("Мутация в популяции. Начало");
                for (Individual individual : newGeneration) {
                    Operator operator = Operator.selectOperator(operators.get(operatorType));
                    operator.apply(individual);
                }
                logger.info("Мутация в популяции. Окончание");
                break;
        }

    }

    public void applyOperatorWithRelated(Map<OperatorSettings, Operator> operators,
                                         OperatorType mainOperatorType) {
        logger.info("Применение связанных операторов в популяции. Начало");
        for (Individual individual : newGeneration) {
            Operator operator = operators.get(individual.getOperatorsSettings().get(mainOperatorType));
            Map<OperatorType, Map<OperatorSettings, Operator>> relatedOperators = operator.getRelatedOperators();
            if (relatedOperators.size() > 0) {
                for (OperatorType relatedOperatorType : relatedOperators.keySet()) {
                    Operator relatedOperator = Operator.selectOperator(operator.getRelatedOperators().get(relatedOperatorType));
                    relatedOperator.apply(individual);
                }
            }
        }
        logger.info("Применение связанных операторов в популяции. Окончание");
    }

    public void calcOperatorsFitness(Map<OperatorType, Map<OperatorSettings, Operator>> operators) {
        logger.info("Подсчёт пригодности операторов в популяции. Начало");
        Operator.calcOperatorFitness(newGeneration, operators);
        logger.info("Подсчёт пригодности операторов в популяции. Окончание");
    }

    public void configureOperators(Map<OperatorType, Map<OperatorSettings, Operator>> operators, int generationsAmount) {
        logger.info("Подсчёт пригодности операторов в популяции. Начало");
        for (Map.Entry<OperatorType, Map<OperatorSettings, Operator>> entry : operators.entrySet()) {
            Operator.setOperatorsProbabilities(entry.getValue(), generationsAmount);
            Operator.setOperatorsDistribution(entry.getValue());
            // а теперь для связанных операторов
            for (Map.Entry<OperatorSettings, Operator> operator : entry.getValue().entrySet()) {
                Map<OperatorType, Map<OperatorSettings, Operator>> relatedOperators = operator.getValue().getRelatedOperators();
                if (relatedOperators.size() > 0) {
                    for (Map.Entry<OperatorType, Map<OperatorSettings, Operator>> relEntry : relatedOperators.entrySet()) {
                        Operator.setOperatorsProbabilities(relEntry.getValue(), generationsAmount);
                        Operator.setOperatorsDistribution(relEntry.getValue());
                    }
                }
            }
        }
        logger.info("Подсчёт пригодности операторов в популяции. Окончание");
    }

    public Individual getBestIndividual() {
        return bestIndividual;
    }

    public void setIndividualsAmount(Integer individualsAmount) {
        this.individualsAmount = individualsAmount;
    }

}
