package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.RecombinationSettings;
import util.RandomUtils;

import java.util.ArrayList;
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

    public List<Individual> recombine(List<Individual> individuals, Integer individualsAmount) {
        logger.info(settings.toString());
        switch (settings.getRecombinationType()) {
            case TYPICAL:
                return typicalRecombination(individuals, individualsAmount);
        }
        return individuals;
    }

    private List<Individual> typicalRecombination(List<Individual> individuals, Integer individualsAmount) {
        int size = individuals.get(0).getDimension();
        List<Individual> children = new ArrayList<>();
        // выбираем родителей попарно
        for (int i = 0; i < individualsAmount * 2; i += 2) {
            Boolean whoIsFirst = RandomUtils.random.nextBoolean();
            if (whoIsFirst) {
                children.add(generateChild(individuals.get(i), individuals.get(i + 1), size));
            } else {
                children.add(generateChild(individuals.get(i + 1), individuals.get(i), size));
            }
        }
        return children;
    }

    private Individual generateChild(Individual parent0, Individual parent1, int size) {
        logger.debug("Первый родитель: " + parent0.toString());
        logger.debug("Второй родитель: " + parent1.toString());
        Individual child = new Individual();
        List<Integer> phenotype = new ArrayList<>();
        int index0 = RandomUtils.getRandomIndexExclude(null, size);
        int index1 = RandomUtils.getRandomIndexExclude(index0, size);
        if (index0 < index1) {
            // копируем всё, что в промежутке между index0 и index1
            for (int i = index0; i <= index1; i++) {
                phenotype.add(parent0.getElementByIndex(i));
            }
            fillChildBySecondParent(parent1, phenotype, index1, size);
        } else {
            // копируем всё, исключая то, что в промежутке между index1 и index0
            for (int i = index0; i < size; i++) {
                phenotype.add(parent0.getElementByIndex(i));
            }
            for (int i = 0; i <= index1; i++) {
                phenotype.add(parent0.getElementByIndex(i));
            }
            fillChildBySecondParent(parent1, phenotype, index0, size);
        }
        child.setDimension(size);
        child.setPhenotype(phenotype);
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
        for (int i = 0; i <= index; i++) {
            Integer code = parent.getElementByIndex(i);
            if (phenotype.indexOf(code) == -1) {
                phenotype.add(code);
            }
        }
    }

    public RecombinationSettings getSettings() {
        return settings;
    }

    public void setSettings(RecombinationSettings settings) {
        this.settings = settings;
    }

}
