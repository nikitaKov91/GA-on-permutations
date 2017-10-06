package algorithm;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.MutationSettings;
import util.MutationType;
import util.RandomUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static util.RandomUtils.*;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class Mutation {

    private static Logger logger = LoggerFactory.getLogger(Problem.class);

    private MutationSettings settings = new MutationSettings();
    private int dimension;

    public void init(String[] params, Integer problemDimension) {
        settings.init(params);
        if (settings.getMutationType() == MutationType.CUSTOM) {
            calcMutationProbability(problemDimension);
        }
    }

    public void calcMutationProbability(Integer problemDimension) {
        logger.debug("Считаем вероятность мутации");
        for (int i = 0; i < problemDimension; i++) {
            dimension += i;
        }
        double probability = 0.5/dimension;
        switch (settings.getMutationProbabilityType()) {
            case VERY_LOW:
                probability *= 0.2;
                break;
            case LOW:
                probability *= 0.5;
                break;
            case HIGH:
                probability *= 2;
                break;
            case VERY_HIGH:
                probability *= 5;
                break;
        }
        settings.setMutationProbability(probability);
        logger.debug("Полученная вероятность: " + probability);
    }

    public void mutate(List<Individual> individuals) {
        logger.info(settings.toString());
        int size = individuals.get(0).getDimension();
        for (Individual individual : individuals) {
            if (isRunMutation()) {
                logger.debug("Запускаем мутацию индивида: " + individual.toString());
                if (settings.getMutationType() == MutationType.TYPICAL) {
                    typicalMutation(individual, size);
                } else if (settings.getMutationType() == MutationType.CUSTOM) {
                    customMutation(individual, size);
                }
                logger.debug("Полученный индивид: " + individual.toString());
            }
        }
    }

    public void typicalMutation(Individual individual, int size) {
        int index0 = getRandomIndexExclude(null, size);
        int index1 = getRandomIndexExclude(null, size);
        logger.debug("Первый индекс: " + index0);
        logger.debug("Второй индекс: " + index1);
        if (index0 != index1) {
            Collections.swap(individual.getPhenotype(), index0, index1);
        }
    }

    public void customMutation(Individual individual, int size) {
        // заполняем лист всевозможных перестановок
        List<Pair<Integer, Integer>> permutations = new ArrayList();
        for (int i = 0 ; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                permutations.add(new Pair(i, j));
            }
        }
        // для каждой перестановки определяем, произойдёт ли она
        List<Pair<Integer, Integer>> delete = new ArrayList();
        for (int i = 0 ; i < permutations.size(); i++) {
            if (settings.getMutationProbability() < random.nextDouble()) {
                delete.add(permutations.get(i));
            }
        }
        // удаляем все несостоявщиеся
        permutations.removeAll(delete);
        // оставшиеся перемешиваем с помощью метода Фишера-Йетса
        for (int i = permutations.size() - 1; i >= 0; i--) {
            int j = RandomUtils.random.nextInt(i + 1);
            Collections.swap(permutations, i, j);
        }
        logger.debug("Перестановки в мутации: " + permutations);
        for (Pair<Integer, Integer> pair : permutations) {
            Collections.swap(individual.getPhenotype(), pair.getKey(), pair.getValue());
        }
    }

    public boolean isRunMutation() {
        if (settings.getMutationType() == MutationType.CUSTOM) {
            return true;
        } else if (settings.getMutationType() == MutationType.TYPICAL) {
            return settings.getMutationProbability() >= random.nextDouble() ? true : false;
        }
        return false;
    }

    public MutationSettings getSettings() {
        return settings;
    }

}