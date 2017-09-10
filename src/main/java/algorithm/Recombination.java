package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.RecombinationSettings;
import util.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class Recombination {

    private static Logger logger = LoggerFactory.getLogger(Problem.class);

    private RecombinationSettings settings = new RecombinationSettings();

    public void init(String[] params) {
        settings.init(params);
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
        Random random = new Random();
        List<Individual> children = new ArrayList<>();
        // выбираем родителей попарно
        for (int i = 0; i < individualsAmount * 2; i += 2) {
            Boolean whoIsFirst = random.nextBoolean();
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
        int index0 = RandomUtils.getRandomIndexExclude(null, size - 1, false);
        int index1 = RandomUtils.getRandomIndexExclude(index0, size - 1, false);
        if (index0 < index1) {
            // копируем всё, что в промежутке между index0 и index1
            for (int i = index0; i <= index1; i++) {
                phenotype.add(parent0.getElementByIndex(i));
            }
        } else {
            // копируем всё, исключая то, что в промежутке между index1 и index0
            for (int i = 0; i <= index1; i++) {
                phenotype.add(parent0.getElementByIndex(i));
            }
            for (int i = index0; i < size; i++) {
                phenotype.add(parent0.getElementByIndex(i));
            }
        }
        // из фенотипа второго родителя надо взять те, что не попали к ребёнку,
        // причём в том же порядке, в котором они идут у второго родителя
        for (Integer code : parent1.getPhenotype()) {
            if (phenotype.indexOf(code) == -1) {
                phenotype.add(code);
            }
        }
        child.setDimension(size);
        child.setPhenotype(phenotype);
        logger.debug("Полученный ребёнок: " + child.toString());
        return child;
    }

    public RecombinationSettings getSettings() {
        return settings;
    }

    public void setSettings(RecombinationSettings settings) {
        this.settings = settings;
    }

}
