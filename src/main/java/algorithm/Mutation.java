package algorithm;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.MutationSettings;
import util.MutationType;
import util.OperatorType;
import util.RandomUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static util.RandomUtils.*;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class Mutation extends Operator {

    private static Logger logger = LoggerFactory.getLogger(Mutation.class);

    private MutationSettings settings;

    public Mutation(MutationSettings settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "Mutation{" +
                "settings=" + settings +
                '}';
    }

    public void innerMutation(Individual individual, int size) {
        logger.debug("Запускаем мутацию индивида: " + individual.toString());
        switch (settings.getMutationType()) {
            case BY_2_EXCHANGE:
                mutationBy2Exchange(individual, size);
                break;
            case BY_INSERTION:
                mutationByInsertion(individual, size);
                break;
            case BY_INVERSION:
                mutationByInversion(individual, size);
                break;
            case BY_SHIFTING:
                mutationByShifting(individual, size);
                break;
        }
        logger.debug("Полученный индивид: " + individual.toString());
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
            if (index0 < index1) {
                int numberOfSwaps = Math.abs(index1 - index0) / 2;
                for (int i = 0; i <= numberOfSwaps; i++) {
                    Collections.swap(individual.getPhenotype(), index0 + i, index1 - i);
                }
            } else {
                List<Integer> newPhenotype = new ArrayList<>();
                List<Integer> part = new ArrayList<>();
                // вырезаем нужную часть где-то в середине
                for (int i = index1 + 1; i < index0; i++) {
                    part.add(individual.getElementByIndex(i));
                }
                // забираем оставшееся
                newPhenotype.addAll(individual.getPhenotype().subList(0, index1 + 1));
                newPhenotype.addAll(individual.getPhenotype().subList(index0, size));
                // меняем местами
                int numberOfSwaps = (size - (index0 - index1)) / 2;
                for (int i = 0; i <= numberOfSwaps; i++) {
                    Collections.swap(newPhenotype, i, newPhenotype.size() - i - 1);
                }
                newPhenotype.addAll(part);
                individual.setPhenotype(newPhenotype);
            }
        }
    }

    public void insert(Individual individual, int index0, int index1) {
        if (index0 != index1) {
            if (index0 < index1) {
                int numberOfSwaps = index1 - index0;
                for (int i = 0; i < numberOfSwaps; i++) {
                    Collections.swap(individual.getPhenotype(), index0 + i, index0 + i + 1);
                }
            } else {
                int numberOfSwaps = index0 - index1;
                for (int i = 0; i < numberOfSwaps; i++) {
                    Collections.swap(individual.getPhenotype(), index0 - i, index0 - i - 1);
                }
            }
        }
    }

    public void mutationByInsertion(Individual individual, int size) {
        int index0 = getRandomIndexExclude(null, size);
        int index1 = getRandomIndexExclude(null, size);
        logger.debug("Первый индекс: " + index0);
        logger.debug("Второй индекс: " + index1);
        insert(individual, index0, index1);
    }

    public void mutationByShifting(Individual individual, int size) {
        int index0 = getRandomIndexExclude(null, size);
        int index1 = getRandomIndexExclude(null, size);
        logger.debug("Первый индекс: " + index0);
        logger.debug("Второй индекс: " + index1);
        if (index0 == index1) {
            int index2 = getRandomIndexExclude(index0, size);
            logger.debug("Третий индекс: " + index2);
            insert(individual, index0, index2);
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
        return mutationProbability >= random.nextDouble() ? true : false;
    }

    public MutationSettings getSettings() {
        return settings;
    }

    @Override
    public void apply(List<Individual> individuals, List<Individual> parents, int parentsAmountl) {
        throw new IllegalArgumentException("Мутация не может быть применена с таким набором аргументов");
    }

    @Override
    public void apply(Individual individual) {
        logger.info(settings.toString());
        individual.getOperatorsSettings().put(OperatorType.MUTATION, settings);
        int size = individual.getDimension();
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