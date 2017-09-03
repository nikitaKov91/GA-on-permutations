package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class Individual {

    private static Logger logger = LoggerFactory.getLogger(Individual.class);

    private List<Integer> phenotype;
    private Double objectiveFunctionValue;
    private Double suitability;
    private int dimension;

    public Individual() {
        super();
    }

    public Individual(List<Integer> phenotype, Double suitability, Double objectiveFunctionValue, int dimension) {
        this.phenotype = phenotype;
        this.suitability = suitability;
        this.objectiveFunctionValue = objectiveFunctionValue;
        this.dimension = dimension;
    }

    /**
     * создание индивида с поомщью метода Фишера-Йетса
     * @param individualDimension - размерность индивида
     * @return представителя класса
     */
    public static Individual createIndividual(Integer individualDimension) {
        logger.info("Создание индивида. Начало");
        logger.debug("Размерность: " + individualDimension);

        Individual individual = new Individual();
        individual.setDimension(individualDimension);
        List<Integer> phenotype = new ArrayList<>();
        for (int i = 0; i < individualDimension; i++) {
            phenotype.add(i);
        }
        Random random = new Random();
        for (int i = individualDimension - 1; i >= 0; i--) {
            int j = random.nextInt(individualDimension);
            Collections.swap(phenotype, i, j);
        }
        individual.setPhenotype(phenotype);

        logger.info("Создание индивида. Окончание");
        return individual;
    }

    public void calcSuitability() {
        logger.debug("Подсчёт значения функции пригодности индивида. Начало");
        suitability = Problem.getInstance().calcFitnessFunction(this);
        logger.debug("Полученное значение: " + suitability);
        logger.debug("Значение оптимизируемой функции: " + objectiveFunctionValue);
        logger.debug("Подсчёт значения функции пригодности индивида. Окончание");
    }

    public Integer getElementByIndex(int index) {
        return phenotype.get(index);
    }

    public String toString() {
        return "(" + phenotype.stream().map(Object::toString)
                .collect(Collectors.joining(", ")) + ")";
    }

    public static Individual clone(Individual individual) {
        return new Individual(individual.phenotype, individual.suitability,
                individual.objectiveFunctionValue, individual.dimension);
    }

    public static int getIndexOfBest(List<Individual> individuals) {
        int i = 0;
        int index = 0;
        Double bestSuitability = 0d;
        for (Individual individual : individuals) {
            if (individual.getSuitability() > bestSuitability) {
                bestSuitability = individual.getSuitability();
                index = i;
            }
            i++;
        }
        return index;
    }

    public List<Integer> getPhenotype() {
        return phenotype;
    }

    public void setPhenotype(List<Integer> phenotype) {
        this.phenotype = phenotype;
    }

    public Double getSuitability() {
        return suitability;
    }

    public void setSuitability(Double suitability) {
        this.suitability = suitability;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public Double getObjectiveFunctionValue() {
        return objectiveFunctionValue;
    }

    public void setObjectiveFunctionValue(Double objectiveFunctionValue) {
        this.objectiveFunctionValue = objectiveFunctionValue;
    }
}
