package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.RecombinationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class Recombination {

    private static Logger logger = LoggerFactory.getLogger(Problem.class);

    private RecombinationType recombinationType;

    public void init (String[] settings) {
        recombinationType = RecombinationType.valueOf(settings[0]);
        switch (recombinationType) {
            case TYPICAL:
                break;
            default:
                throw new IllegalArgumentException("Передан неверный тип рекомбинации: " + settings[0] +
                        " допустимые значения: " + Arrays.toString(RecombinationType.values()));

        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Тип селекции: ");
        switch (recombinationType) {
            case TYPICAL:
                sb.append("стандартная");
                break;
        }
        return sb.toString();
    }

    public List<Individual> recombination(List<Individual> individuals, Integer individualsAmount) {
        logger.info(toString());
        switch (recombinationType) {
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
                children.add(generateChild(random, individuals.get(i), individuals.get(i + 1), size));
            } else {
                children.add(generateChild(random, individuals.get(i + 1), individuals.get(i), size));
            }
        }
        return children;
    }

    private Individual generateChild(Random random, Individual parent0, Individual parent1, int size) {
        logger.info("Первый родитель: " + parent0.toString());
        logger.info("Второй родитель: " + parent1.toString());
        Individual child = new Individual();
        List<Integer> phenotype = new ArrayList<>();
        int index0 = getRandomIndexExclude(null, random, size);
        int index1 = getRandomIndexExclude(index0, random, size);
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
        logger.info("Полученный ребёнок: " + child.toString());
        return child;
    }

    private int getRandomIndexExclude(Integer excludeIndex, Random random, int bound) {
        Integer index;
        do {
            index = random.nextInt(bound - 1);
        } while (index == 0 || index.equals(excludeIndex));
        return index;
    }
}
