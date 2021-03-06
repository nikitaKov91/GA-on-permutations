package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.MutationSettings;
import settings.OperatorSettings;
import settings.RecombinationSettings;
import settings.SelectionSettings;
import util.OperatorType;
import util.RandomUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class Individual implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(Individual.class);

    private List<Integer> phenotype;
    private Double objectiveFunctionValue;
    private Double fitness;
    private int dimension;
    private Map<OperatorType, OperatorSettings> operatorsSettings = new HashMap<>();
    /**
     * создание индивида с помощью метода Фишера-Йетса
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
        for (int i = individualDimension - 1; i >= 0; i--) {
            int j = RandomUtils.random.nextInt(i + 1);
            Collections.swap(phenotype, i, j);
        }
        individual.setPhenotype(phenotype);

        logger.info("Создание индивида. Окончание");
        return individual;
    }

    public void calcFitness(Problem problem) {
        logger.debug("Подсчёт значения функции пригодности индивида. Начало");
        objectiveFunctionValue = problem.calcObjectiveFunction(this);
        logger.debug("Значение оптимизируемой функции: " + objectiveFunctionValue);
        fitness = 10000d / (1 + objectiveFunctionValue);
        logger.debug("Полученное значение: " + fitness);
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(individual);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Individual) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Integer> getPhenotype() {
        return phenotype;
    }

    public void setPhenotype(List<Integer> phenotype) {
        this.phenotype = phenotype;
    }

    public Double getFitness() {
        return fitness;
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

    public Map<OperatorType, OperatorSettings> getOperatorsSettings() {
        return operatorsSettings;
    }

}
