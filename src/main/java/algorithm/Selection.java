package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.SelectionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class Selection {

    private static Logger logger = LoggerFactory.getLogger(Problem.class);

    private SelectionType selectionType;
    private Integer tournamentSize;

    public void init (String[] settings) {
        selectionType = SelectionType.valueOf(settings[0]);
        switch (selectionType) {
            case PROPORTIONAL:

                break;
            case RANKING:

                break;
            case TOURNAMENT:
                tournamentSize = Integer.parseInt(settings[1]);
                break;
            default:
                throw new IllegalArgumentException("Передан неверный тип селекции: " + settings[0] +
                        " допустимые значения: " + Arrays.toString(SelectionType.values()));

        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Тип селекции: ");
        switch (selectionType) {
            case PROPORTIONAL:
                sb.append("пропорциональная");
                break;
            case RANKING:
                sb.append("ранговая");
                break;
            case TOURNAMENT:
                sb.append("турнирная");
                sb.append(". Размер турнира: " + tournamentSize);
                break;
        }
        return sb.toString();
    }

    public List<Individual> selection(List<Individual> individuals, Integer individualsAmount) {
        logger.info(toString());
        switch (selectionType) {
            case PROPORTIONAL:
                return proportionalSelection(individuals, individualsAmount);
            case RANKING:
                return rankingSelection(individuals, individualsAmount);
            case TOURNAMENT:
                return tournamentSelection(individuals, individualsAmount);
        }
        return individuals;
    }

    private List<Individual> proportionalSelection(List<Individual> individuals, Integer individualsAmount) {
        return individuals;
    }

    private List<Individual> rankingSelection(List<Individual> individuals, Integer individualsAmount) {
        return individuals;
    }

    /**
     * турнирная селекция, выбор K индивидов из N и взятие лучшего из K
     * @param individuals - популяция индивидов
     * @param individualsAmount - кол-во индивидов
     * @return отобранные родители
     */
    private List<Individual> tournamentSelection(List<Individual> individuals, Integer individualsAmount) {
        Random random = new Random();
        List<Individual> parents = new ArrayList<>();
        logger.info("Нужное количество родителей: " + individualsAmount * 2);
        for (int i = 0; i < individualsAmount * 2; i++) {
            int maxIndex = 0;
            Double maxSuitability = 0d;
            List<Integer> indexes = new ArrayList<>();
            for (int j = 0; j < tournamentSize; j++) {
                int index = getRandomIndexExclude(indexes, random, individualsAmount);
                indexes.add(index);
                // выбираем лучшего
                Double suitability = individuals.get(index).getSuitability();
                if (suitability > maxSuitability) {
                    maxIndex = index;
                    maxSuitability = suitability;
                }
            }
            Individual chosen = individuals.get(maxIndex);
            logger.info("Отобранный индивид: " + chosen.toString());
            logger.info("Его пригодность: " + chosen.getSuitability());
            parents.add(chosen);
        }
        return parents;
    }

    /**
     * получение случайного индекса
     * @param indexes - уже имеющиеся индексы
     * @param random - объект для получения случайного числа
     * @param individualsAmount - общее кол-во (ограничение для random)
     * @return нужный индекс
     */
    private int getRandomIndexExclude(List<Integer> indexes, Random random, Integer individualsAmount) {
        int index;
        do {
            index = random.nextInt(individualsAmount);
        } while (indexes.indexOf(index) != -1);
        return index;
    }

    public SelectionType getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(SelectionType selectionType) {
        this.selectionType = selectionType;
    }

    public Integer getTournamentSize() {
        return tournamentSize;
    }

    public void setTournamentSize(Integer tournamentSize) {
        this.tournamentSize = tournamentSize;
    }
}
