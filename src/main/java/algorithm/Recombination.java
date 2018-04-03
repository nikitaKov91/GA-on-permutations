package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.RecombinationSettings;
import util.OperatorType;
import util.RandomUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class Recombination extends Operator {

    private static Logger logger = LoggerFactory.getLogger(Problem.class);

    private RecombinationSettings settings;

    public Recombination(RecombinationSettings settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "Recombination{" +
                "settings=" + settings +
                '}';
    }

    private void typicalRecombination(List<Individual> individuals, List<Individual> parents, int index) {
        Individual child;
        // случайным образом выбираем порядок родителей
        if (RandomUtils.random.nextBoolean()) {
            child = generateChild(parents.get(index), parents.get(index + 1));
        } else {
            child = generateChild(parents.get(index + 1), parents.get(index));
        }
        individuals.add(child);
    }

    private Individual generateChild(Individual parent0, Individual parent1) {
        logger.debug("Первый родитель: " + parent0.toString());
        logger.debug("Второй родитель: " + parent1.toString());
        Individual child = new Individual();
        List<Integer> phenotype = new ArrayList<>();
        int size = parent0.getDimension();
        int index0 = RandomUtils.getRandomIndexExclude(null, size);
        int index1 = RandomUtils.getRandomIndexExclude(index0, size);
        if (index0 < index1) {
            // копируем всё, что в промежутке между index0 и index1
            for (int i = index0; i <= index1; i++) {
                phenotype.add(parent0.getElementByIndex(i));
            }
            fillChildBySecondParent(parent1, phenotype, index1 + 1, size);
        } else {
            // копируем всё, исключая то, что в промежутке между index1 и index0
            for (int i = index0; i < size; i++) {
                phenotype.add(parent0.getElementByIndex(i));
            }
            for (int i = 0; i <= index1; i++) {
                phenotype.add(parent0.getElementByIndex(i));
            }
            fillChildBySecondParent(parent1, phenotype, index1 + 1, size);
        }
        child.setDimension(size);
        child.setPhenotype(phenotype);
        for (OperatorType operatorType : parent0.getOperatorsSettings().keySet()) {
            child.getOperatorsSettings().put(operatorType, parent1.getOperatorsSettings().get(operatorType));
        }
        child.getOperatorsSettings().put(OperatorType.RECOMBINATION, settings);
        logger.debug("Полученный ребёнок: " + child.toString());
        return child;
    }

    private void fillChildBySecondParent(Individual parent, List<Integer> phenotype, int index, int size) {
        for (int i = index; i < size; i++) {
            Integer code = parent.getElementByIndex(i);
            if (phenotype.indexOf(code) == -1) {
                phenotype.add(code);
            }
        }
        for (int i = 0; i < index; i++) {
            Integer code = parent.getElementByIndex(i);
            if (phenotype.indexOf(code) == -1) {
                phenotype.add(code);
            }
        }
    }

    public RecombinationSettings getSettings() {
        return settings;
    }

    @Override
    public void apply(Individual individual) {
        throw new IllegalArgumentException("Рекомендация не может быть применена с таким набором аргументов");
    }

    @Override
    public void apply(List<Individual> individuals, List<Individual> parents, int index) {
        logger.info(settings.toString());
        switch (settings.getRecombinationType()) {
            case TYPICAL:
                typicalRecombination(individuals, parents, index);
                break;
        }
    }

    public void setSettings(RecombinationSettings settings) {
        this.settings = settings;
    }

}
