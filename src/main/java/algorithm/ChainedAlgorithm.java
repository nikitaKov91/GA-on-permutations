package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.OperatorSettings;
import util.OperatorFactory;
import util.OperatorType;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChainedAlgorithm extends Algorithm {

    private static Logger logger = LoggerFactory.getLogger(ChainedAlgorithm.class);

    private static final OperatorType RELATED_OPERATOR_TYPE = OperatorType.MUTATION;

    @Override
    public void initOperatorSettings(String operatorsFolder) throws IOException {
        Operator.initOperatorSettings(operators, operatorsFolder);
        // для каждой селекции добавляем список связанных мутаций со своими вероятностями
        Map<OperatorSettings, Operator> selections = operators.get(OperatorType.SELECTION);
        List<List<String>> mutationsSettings = Operator.getOperatorsSettings(OperatorType.MUTATION, operatorsFolder);
        for (Map.Entry<OperatorSettings, Operator> selection : selections.entrySet()) {
            Map<OperatorSettings, Operator> mutations = new HashMap<>();
            for (List<String> setting : mutationsSettings) {
                Operator operator = OperatorFactory.createOperator(setting);
                OperatorSettings operatorSettings = operator.getSettings();
                mutations.put(operatorSettings, operator);
            }
            selection.getValue().getRelatedOperators().put(OperatorType.MUTATION, mutations);
            for (Map.Entry<OperatorType, Map<OperatorSettings, Operator>> entry : selection.getValue().getRelatedOperators().entrySet()) {
                Operator.setOperatorsInitialProbabilities(entry.getValue());
                Operator.setOperatorsDistribution(entry.getValue());
            }
        }
    }

    @Override
    public void process(int problemNumber, boolean isWriteProbabilitiesInFiles) throws IOException {
        logger.info("Алгоритм. Начало");
        population.init(problem);
        if (isWriteProbabilitiesInFiles) {
            Operator.writeHeadProbabilitiesInFiles(operators, problemNumber, RELATED_OPERATOR_TYPE);
        }
        do {
            countOfGenerations += 1;
            population.applyOperator(OperatorType.SELECTION, operators);
            population.applyOperator(OperatorType.RECOMBINATION, operators);
            population.applyOperatorWithRelated(operators.get(OperatorType.SELECTION), RELATED_OPERATOR_TYPE);
            population.calcFitness(problem);
            population.findBest();
            population.calcOperatorsFitness(operators);
            population.configureOperators(operators, settings.getGenerationsAmount());
            if (isWriteProbabilitiesInFiles) {
                if (countOfGenerations % AMOUNT_OF_ITERATIONS_FOR_PROBABILITIES_OUTPUT == 0) {
                    Operator.writeProbabilitiesInFiles(operators, countOfGenerations, problemNumber, RELATED_OPERATOR_TYPE);
                }
            }
        } while (stopCriterion());
        logger.info("Алгоритм. Окончание");
    }

    @Override
    public void process(int problemNumber, boolean isWriteProbabilitiesInFiles, boolean isUseElitism) throws IOException {
        logger.info("Алгоритм. Начало");
        Integer amountOfReplace = (int) Math.ceil(problem.getDimension() * REPLACEMENT_PERCENT / 100d);
        population.init(problem);
        if (isWriteProbabilitiesInFiles) {
            Operator.writeHeadProbabilitiesInFiles(operators, problemNumber, RELATED_OPERATOR_TYPE);
        }
        do {
            countOfGenerations += 1;
            population.applyOperator(OperatorType.SELECTION, operators);
            population.applyOperator(OperatorType.RECOMBINATION, operators);
            population.applyOperatorWithRelated(operators.get(OperatorType.SELECTION), RELATED_OPERATOR_TYPE);
            population.calcFitness(problem);
            population.replacement();
            population.findBest();
            population.calcOperatorsFitness(operators);
            population.configureOperators(operators, settings.getGenerationsAmount());
            if (isWriteProbabilitiesInFiles) {
                if (countOfGenerations % AMOUNT_OF_ITERATIONS_FOR_PROBABILITIES_OUTPUT == 0) {
                    Operator.writeProbabilitiesInFiles(operators, countOfGenerations, problemNumber, RELATED_OPERATOR_TYPE);
                }
            }
            population.rememberGeneration(amountOfReplace);
        } while (stopCriterion());
        logger.info("Алгоритм. Окончание");
    }

}
