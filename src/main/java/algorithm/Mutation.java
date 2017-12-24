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
        if (settings.getMutationType() != MutationType.BY_2_EXCHANGE) {
            calcMutationProbability(problemDimension);
        }
    }

    public void calcMutationProbability(Integer problemDimension) {
        logger.debug("Считаем вероятность мутации");
        double probability = 0;
        if (settings.getMutationType() == MutationType.CUSTOM) {
            for (int i = 0; i < problemDimension; i++) {
                dimension += i;
            }
            probability = 0.5/dimension;
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
        } else {
            switch (settings.getMutationProbabilityType()) {
                case VERY_LOW:
                    probability = 0.2;
                    break;
                case LOW:
                    probability = 0.5;
                    break;
                case AVERAGE:
                    probability = 1;
                    break;
                case HIGH:
                    probability = 1.5;
                    break;
                case VERY_HIGH:
                    probability = 2;
                    break;
            }
        }
        
        settings.setMutationProbability(probability);
        logger.debug("Полученная вероятность: " + probability);
    }

    public void innerMutation(Individual individual, int size) {
        logger.debug("Запускаем мутацию индивида: " + individual.toString());
        if (settings.getMutationType() == MutationType.BY_2_EXCHANGE) {
            mutationBy2Exchange(individual, size);
        } else if (settings.getMutationType() == MutationType.CUSTOM) {
            customMutation(individual, size);
        } else if (settings.getMutationType() == MutationType.BY_INSERTION) {
            mutationByInsertion(individual, size);
        } else if (settings.getMutationType() == MutationType.BY_INVERSION) {
            mutationByInversion(individual, size);
        } else if (settings.getMutationType() == MutationType.BY_SHIFTING) {
            mutationByShifting(individual, size);
        }
        logger.debug("Полученный индивид: " + individual.toString());
    }

    public void mutate(List<Individual> individuals) {
        logger.info(settings.toString());
        int size = individuals.get(0).getDimension();
        for (Individual individual : individuals) {
            if (isRunMutation(settings.getMutationProbability())) {
                innerMutation(individual, size);
            }
            // при вероятности мутации 1.5 индивид мутирует второй раз с вероятностью 50%,
            // при вероятности мутации 2 индивид всегда мутирует дважды
            if (settings.getMutationProbability() > 1) {
                if (isRunMutation(settings.getMutationProbability() - 1)) {
                    innerMutation(individual, size);
                }
            }
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

    public void mutationBy2Exchange(Individual individual, int size) {
        int index0 = getRandomIndexExclude(null, size);
        int index1 = getRandomIndexExclude(null, size);
        logger.debug("Первый индекс: " + index0);
        logger.debug("Второй индекс: " + index1);
        if (index0 != index1) {
            Collections.swap(individual.getPhenotype(), index0, index1);
        }
    }

    public void mutationByInversion(Individual individual, int size) {
        int index0 = getRandomIndexExclude(null, size);
        int index1 = getRandomIndexExclude(null, size);
        logger.debug("Первый индекс: " + index0);
        logger.debug("Второй индекс: " + index1);
        if (index0 != index1) {
            int numberOfSwaps = Math.abs(index1 - index0) / 2;
            if (index0 < index1) {
                for (int i = 0; i <= numberOfSwaps; i++) {
                    Collections.swap(individual.getPhenotype(), index0 + i, index1 - i);
                }
            } else {
                for (int i = 1; i < numberOfSwaps; i++) {
                    Collections.swap(individual.getPhenotype(), index1 + i, index0 - i);
                }
            }
        }
    }

    public void insert(Individual individual, int index0, int index1) {
        int numberOfSwaps = index1 - index0;
        for (int i = 0; i < numberOfSwaps; i++) {
            Collections.swap(individual.getPhenotype(), index0 + i, index0 + i + 1);
        }
    }

    public void mutationByInsertion(Individual individual, int size) {
        int index0 = getRandomIndexExclude(null, size);
        int index1 = getRandomIndexExclude(null, size);
        logger.debug("Первый индекс: " + index0);
        logger.debug("Второй индекс: " + index1);
        if (index0 != index1) {
            if (index0 < index1) {
                insert(individual, index0, index1);
            } else {
                insert(individual, index1, index0);
            }
        }
    }

    public void mutationByShifting(Individual individual, int size) {
        int index0 = getRandomIndexExclude(null, size);
        int index1 = getRandomIndexExclude(null, size);
        logger.debug("Первый индекс: " + index0);
        logger.debug("Второй индекс: " + index1);
        if (index0 == index1) {
            int index2 = getRandomIndexExclude(index0, size);
            logger.debug("Третий индекс: " + index2);
            if (index0 != index2) {
                if (index0 < index2) {
                    insert(individual, index0, index2);
                } else {
                    insert(individual, index2, index0);
                }
            }
        } else {
            if (!(index0 == 0 && index1 == size - 1 ||
                  index0 == size - 1 && index1 == 0)) {
                List<Integer> newPhenotype = new ArrayList<>();
                List<Integer> part = new ArrayList<>();
                if (index0 < index1) {
                    // вырезаем нужную часть где-то в середине
                    for (int i = index0; i <= index1; i++) {
                        part.add(individual.getElementByIndex(i));
                    }
                    individual.getPhenotype().removeAll(part);
                    int index2 = getRandomIndexExclude(null, individual.getPhenotype().size());
                    // добавляем остаток в новый фенотип
                    newPhenotype.addAll(individual.getPhenotype().subList(0, index2));
                    newPhenotype.addAll(part);
                    newPhenotype.addAll(individual.getPhenotype().subList(index2, individual.getPhenotype().size()));
                    individual.setPhenotype(newPhenotype);
                } else {
                    // вырезаем нужную часть из конца
                    for (int i = index0; i < size; i++) {
                        part.add(individual.getElementByIndex(i));
                    }
                    // и из начала
                    for (int i = 0; i <= index1; i++) {
                        part.add(individual.getElementByIndex(i));
                    }
                    individual.getPhenotype().removeAll(part);
                    // если индексы соседние - в фенотипе ничего не останется
                    if (individual.getPhenotype().size() == 0) {
                        individual.setPhenotype(part);
                    } else {
                        int index2 = getRandomIndexExclude(null, individual.getPhenotype().size());
                        // добавляем остаток в новый фенотип
                        newPhenotype.addAll(individual.getPhenotype().subList(0, index2));
                        newPhenotype.addAll(part);
                        newPhenotype.addAll(individual.getPhenotype().subList(index2, individual.getPhenotype().size()));
                        individual.setPhenotype(newPhenotype);
                    }
                }
            }
        }
    }

    public boolean isRunMutation(Double mutationProbability) {
        if (settings.getMutationType() == MutationType.CUSTOM) {
            return true;
        } else {
            return mutationProbability >= random.nextDouble() ? true : false;
        }
    }

    public MutationSettings getSettings() {
        return settings;
    }

}