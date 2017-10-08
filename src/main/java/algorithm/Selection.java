package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.SelectionSettings;
import util.RandomUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class Selection {

    private static Logger logger = LoggerFactory.getLogger(Problem.class);

    private SelectionSettings settings = new SelectionSettings();

    public void init(String[] params) {
        settings.init(params);
    }

    public List<Individual> select(List<Individual> individuals, int individualsAmount) {
        logger.info(settings.toString());
        logger.info("Нужное количество родителей: " + individualsAmount * 2);
        switch (settings.getSelectionType()) {
            case PROPORTIONAL:
                return proportionalSelection(individuals, individualsAmount);
            case RANKING:
                return rankingSelection(individuals, individualsAmount);
            case TOURNAMENT:
                return tournamentSelection(individuals, individualsAmount);
        }
        return individuals;
    }

    private List<Individual> proportionalSelection(List<Individual> individuals, int individualsAmount) {
        List<Individual> parents = new ArrayList<>(individualsAmount * 2);
        // считаем суммарную пригодность
        double summarySuitability = 0;
        for (Individual individual : individuals) {
            summarySuitability += individual.getSuitability();
        }
        // набираем родителей
        for (int i = 0; i < individualsAmount * 2; i++) {
            double rollDice = summarySuitability * RandomUtils.random.nextDouble();
            double localSummary = 0;
            for (Individual individual : individuals) {
                // если суммарная пригодность меньше выброшенного числа - индивид становится родителем
                localSummary += individual.getSuitability();
                if (localSummary <= rollDice) {
                    parents.add(individual);
                    break;
                }
            }
        }
        return parents;
    }

    private List<Individual> rankingSelection(List<Individual> individuals, int individualsAmount) {
        return individuals;
    }

    /**
     * турнирная селекция, выбор K индивидов из N и взятие лучшего из K
     * @param individuals - популяция индивидов
     * @param individualsAmount - кол-во индивидов
     * @return отобранные родители
     */
    private List<Individual> tournamentSelection(List<Individual> individuals, int individualsAmount) {
        List<Individual> parents = new ArrayList<>(individualsAmount * 2);
        for (int i = 0; i < individualsAmount * 2; i++) {
            int maxIndex = 0;
            Double maxSuitability = 0d;
            List<Integer> indexes = new ArrayList<>();
            for (int j = 0; j < settings.getTournamentSize(); j++) {
                int index = RandomUtils.getRandomIndexExclude(indexes, individualsAmount);
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
        return parents;
    }

    public SelectionSettings getSettings() {
        return settings;
    }

}
