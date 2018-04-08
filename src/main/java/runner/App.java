package runner;

import algorithm.Algorithm;
import algorithm.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.OperatorSettings;
import util.OperatorType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class App {

    static Logger logger = LoggerFactory.getLogger(App.class);

    // при необходимости передавать в параметрах
    public static final String AUTO = "1";

    public static void main(String[] args) {
        try {
            if ("1".equals(AUTO)) {
                for (int i = 0; i < 3; i++) {
                    List<String> result = new ArrayList<>();
                    for (int k = 0; k <= 99; k++) {
                        Algorithm algorithm = new Algorithm();
                        algorithm.init("problems\\problem" + i + ".txt",
                                "settings\\settings" + i + ".txt");
                        algorithm.initOperatorSettings("operators");
                        algorithm.process();
                        result.add(String.valueOf(algorithm.getPopulation().getBestIndividual().getObjectiveFunctionValue()));
                    }
                    Files.write(Paths.get("results\\result-problem" + i + ".txt"), result);
                }
            } else {
                Map<OperatorType, Map<OperatorSettings, Operator>> allOperators = new HashMap<>();
                Operator.initOperatorSettings(allOperators, "operators");
                for (int i = 0; i < 3; i++) {
                    List<String> result = new ArrayList<>();
                    for (Map.Entry<OperatorSettings, Operator> selection : allOperators.get(OperatorType.SELECTION).entrySet()) {
                        for (Map.Entry<OperatorSettings, Operator> recombination : allOperators.get(OperatorType.RECOMBINATION).entrySet()) {
                            for (Map.Entry<OperatorSettings, Operator> mutation : allOperators.get(OperatorType.MUTATION).entrySet()) {
                                result.add(selection.getValue().toString());
                                result.add(recombination.getValue().toString());
                                result.add(mutation.getValue().toString());
                                StringBuilder sb = new StringBuilder();
                                for (int k = 0; k <= 99; k++) {
                                    Algorithm algorithm = new Algorithm();
                                    algorithm.init("problems\\problem" + i + ".txt",
                                            "settings\\settings" + i + ".txt");
                                    Map<OperatorType, Map<OperatorSettings, Operator>> operators = new HashMap<>();
                                    // селекция
                                    Map<OperatorSettings, Operator> selections = new HashMap<>();
                                    selections.put(selection.getKey(), selection.getValue());
                                    operators.put(OperatorType.SELECTION, selections);
                                    // рекомбинация
                                    Map<OperatorSettings, Operator> recombinations = new HashMap<>();
                                    recombinations.put(recombination.getKey(), recombination.getValue());
                                    operators.put(OperatorType.RECOMBINATION, recombinations);
                                    // мутация
                                    Map<OperatorSettings, Operator> mutations = new HashMap<>();
                                    mutations.put(mutation.getKey(), mutation.getValue());
                                    operators.put(OperatorType.MUTATION, mutations);
                                    algorithm.setOperators(operators);
                                    algorithm.process();
                                    sb.append(String.valueOf(algorithm.getPopulation().getBestIndividual().getObjectiveFunctionValue()));
                                    sb.append("\t");
                                }
                                result.add(sb.toString());
                            }
                        }
                    }
                    Files.write(Paths.get("results\\result-problem" + i + ".txt"), result);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
