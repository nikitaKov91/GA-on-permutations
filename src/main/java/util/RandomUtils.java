package util;

import java.util.List;
import java.util.Random;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class RandomUtils {

    public static Random random = new Random();

    /**
     * получение случайного индекса
     * @param indexes - уже имеющиеся индексы
     * @param bound - общее кол-во (ограничение для random)
     * @return нужный индекс
     */
    public static int getRandomIndexExclude(List<Integer> indexes, Integer bound) {
        int index;
        do {
            index = random.nextInt(bound);
        } while (indexes.indexOf(index) != -1);
        return index;
    }

    /**
     * получение случайного индекса (используется для 2х индексов, чтобы не создавать List)
     * @param excludeIndex - индекс для исключения
     * @param bound - общее кол-во (ограничение для random)
     * @return нужный индекс
     */
    public static int getRandomIndexExclude(Integer excludeIndex, int bound) {
        Integer index;
        do {
            index = random.nextInt(bound);
        } while (index.equals(excludeIndex));
        return index;
    }

}
