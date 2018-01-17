package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.SelectionSettings;
import util.RandomUtils;
import util.RankingSelectionType;

import java.util.*;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class Selection extends Operator {

    private static int initialWeight = 1;

    private static Logger logger = LoggerFactory.getLogger(Problem.class);

    private SelectionSettings settings;

    public Selection(SelectionSettings settings) {
        this.settings = settings;
    }

    private void proportionalSelection(List<Individual> individuals, List<Individual> parents, int parentsAmount) {
        // считаем суммарную пригодность
        double summarySuitability = 0;
        for (Individual individual : individuals) {
            summarySuitability += individual.getSuitability();
        }
        // набираем родителей
        for (int i = 0; i < parentsAmount; i++) {
            double rollDice = summarySuitability * RandomUtils.random.nextDouble();
            double localSummary = 0;
            for (Individual individual : individuals) {
                // если суммарная пригодность меньше выброшенного числа - индивид становится родителем
                localSummary += individual.getSuitability();
                if (localSummary >= rollDice) {
                    parents.add(individual);
                    break;
                }
            }
        }
    }

    class IndividualWrapper {

        Individual individual;
        double rank;

        public IndividualWrapper(Individual individual) {
            this.individual = individual;
        }

    }

    class IndividualWrapperComparator implements Comparator<IndividualWrapper> {

        @Override
        public int compare(IndividualWrapper o1, IndividualWrapper o2) {
            double difference = o1.individual.getSuitability() - o2.individual.getSuitability();
            if (difference == 0) {
                return 0;
            } else if (difference > 0) {
                return 1;
            } else {
                return -1;
            }
        }

    }

    private void linearSelectionRanking(List<IndividualWrapper> individualsSorted) {
        // проставляем ранги
        int i = 0;
        while (i < individualsSorted.size()) {
            double suitability = individualsSorted.get(i).individual.getSuitability();
            int j = i + 1;
            int count = 1;
            // считаем количество индивидов с такой же пригодностью
            while (j < individualsSorted.size() && suitability == individualsSorted.get(j).individual.getSuitability()) {
                count += 1;
                j += 1;
            }
            // если индивидов несколько, рангом будет среднее арифметическое
            double rank = 0;
            for (int k = 1; k <= count; k++) {
                rank += i + k;
            }
            rank /= count;
            individualsSorted.get(i).rank = rank;
            if (count > 1) {
                for (int k = 1; k <= count; k++) {
                    individualsSorted.get(i + k).rank = rank;
                }
            }
            i += count;
        }
    }

    private void exponentialSelectionRanking(List<IndividualWrapper> individualsSorted) {
        // проставляем ранги
        int size = individualsSorted.size();
        int i = size - 1;
        double suitability = -1;
        double rank = -1;
        while (i > 0) {
            if (suitability == individualsSorted.get(i).individual.getSuitability()) {
                individualsSorted.get(i).rank = rank;
            } else {
                rank = initialWeight * Math.pow(settings.getWeight(), size - i - 1);
                suitability = individualsSorted.get(i).individual.getSuitability();
                individualsSorted.get(i).rank = rank;
            }
            i -= 1;
        }
    }

    private List<IndividualWrapper> getSortAndRankedIndividuals(List<Individual> individuals) {
        // оборачиваем инивида, добавляя ранг
        List<IndividualWrapper> individualsSorted = new ArrayList<>(individuals.size());
        for (Individual individual : individuals) {
            individualsSorted.add(new IndividualWrapper(individual));
        }
        // упорядочиваем индивидов по возрастанию пригодности
        Collections.sort(individualsSorted, new IndividualWrapperComparator());
        if (settings.getRankingSelectionType() == RankingSelectionType.EXPONENTIAL) {
            exponentialSelectionRanking(individualsSorted);
        } else if (settings.getRankingSelectionType() == RankingSelectionType.LINEAR) {
            linearSelectionRanking(individualsSorted);
        }
        return individualsSorted;
    }

    private void rankingSelection(List<Individual> individuals, List<Individual> parents, int parentsAmount) {
        List<IndividualWrapper> individualsSorted = getSortAndRankedIndividuals(individuals);
        // считаем суммарную пригодность
        double summaryRank = 0;
        for (IndividualWrapper individualWrapper : individualsSorted) {
            summaryRank += individualWrapper.rank;
        }
        // набираем родителей
        for (int i = 0; i < parentsAmount; i++) {
            double rollDice = summaryRank * RandomUtils.random.nextDouble();
            double localSummary = 0;
            for (IndividualWrapper individualWrapper : individualsSorted) {
                // если суммарная пригодность меньше выброшенного числа - индивид становится родителем
                localSummary += individualWrapper.rank;
                if (localSummary >= rollDice) {
                    parents.add(individualWrapper.individual);
                    break;
                }
            }
        }
    }

    /**
     * турнирная селекция, выбор K индивидов из N и взятие лучшего из K
     * @param individuals - популяция индивидов
     * @param parentsAmount - необходимое кол-во родителей
     */
    private void tournamentSelection(List<Individual> individuals, List<Individual> parents, int parentsAmount) {
        for (int i = 0; i < parentsAmount; i++) {
            int maxIndex = 0;
            Double maxSuitability = 0d;
            List<Integer> indexes = new ArrayList<>();
            for (int j = 0; j < settings.getTournamentSize(); j++) {
                int index = RandomUtils.getRandomIndexExclude(indexes, individuals.size());
                indexes.add(index);
                // выбираем лучшего
                Double suitability = individuals.get(index).getSuitability();
                if (suitability > maxSuitability) {
                    maxIndex = index;
                    maxSuitability = suitability;
                }
            }
            Individual chosen = individuals.get(maxIndex);
            logger.debug("Отобранный индивид: " + chosen.toString());
            logger.debug("Его пригодность: " + chosen.getSuitability());
            parents.add(chosen);
        }
    }

    public SelectionSettings getSettings() {
        return settings;
    }

    @Override
    public void apply(Individual individual) {
        throw new IllegalArgumentException("Селекция не может быть применена с таким набором аргументов");
    }

    @Override
    public void apply(List<Individual> individuals, List<Individual> parents, int parentsAmount) {
        logger.info(settings.toString());
        logger.info("Нужное количество родителей: " + parentsAmount);
        switch (settings.getSelectionType()) {
            case PROPORTIONAL:
                proportionalSelection(individuals, parents, parentsAmount);
                break;
            case RANKING:
                rankingSelection(individuals, parents, parentsAmount);
                break;
            case TOURNAMENT:
                tournamentSelection(individuals, parents, parentsAmount);
                break;
        }
    }

}
