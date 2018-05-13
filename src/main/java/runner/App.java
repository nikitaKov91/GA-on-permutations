package runner;

import algorithm.Algorithm;
import algorithm.ChainedAlgorithm;
import algorithm.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.OperatorSettings;
import util.OperatorType;

import java.io.IOException;
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

    public static final int runsAmount = 100;
    static Logger logger = LoggerFactory.getLogger(App.class);
    static boolean isWriteProbabilitiesInFiles;
    static int selfConfigurationType = 0;
    static boolean isUseElitism = true;

    public static void runProblem(Map<OperatorType, Map<OperatorSettings, Operator>> allOperators, int problemNumber) throws IOException {
        List<String> results = new ArrayList<>();
        for (Map.Entry<OperatorSettings, Operator> selection : allOperators.get(OperatorType.SELECTION).entrySet()) {
            for (Map.Entry<OperatorSettings, Operator> recombination : allOperators.get(OperatorType.RECOMBINATION).entrySet()) {
                for (Map.Entry<OperatorSettings, Operator> mutation : allOperators.get(OperatorType.MUTATION).entrySet()) {
                    results.add(selection.getValue().toString());
                    results.add(recombination.getValue().toString());
                    results.add(mutation.getValue().toString());
                    StringBuilder sb = new StringBuilder();
                    Double min = Double.MAX_VALUE;
                    Double sum = 0.d;
                    for (int k = 0; k < runsAmount; k++) {
                        Algorithm algorithm = new Algorithm();
                        algorithm.init("problems\\problem" + problemNumber + ".txt",
                                "settings\\settings" + problemNumber + ".txt");
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
                        if (isUseElitism) {
                            algorithm.process(problemNumber, k == 0 ? isWriteProbabilitiesInFiles : false, true);
                        } else {
                            algorithm.process(problemNumber, k == 0 ? isWriteProbabilitiesInFiles : false);
                        }
                        Double result = algorithm.getPopulation().getBestIndividual().getObjectiveFunctionValue();
                        sum += result;
                        if (result < min) {
                            min = result;
                        }
                        sb.append(result);
                        sb.append("\t");
                    }
                    results.add(sb.toString());
                    results.add("best: " + min);
                    results.add("average: " + sum / runsAmount);
                }
            }
        }
        Files.write(Paths.get("results\\result-problem" + problemNumber + ".txt"), results);
    }

    public static void runProblem(int problemNumber) throws IOException {
        List<String> results = new ArrayList<>();
        Double min = Double.MAX_VALUE;
        Double sum = 0.d;
        for (int k = 0; k < runsAmount; k++) {
            Double result = null;
            if (selfConfigurationType == 1) {
                Algorithm algorithm = new Algorithm();
                algorithm.init("problems\\problem" + problemNumber + ".txt",
                        "settings\\settings" + problemNumber + ".txt");
                algorithm.initOperatorSettings("operators");
                if (isUseElitism) {
                    algorithm.process(problemNumber, k == 0 ? isWriteProbabilitiesInFiles : false, true);
                } else {
                    algorithm.process(problemNumber, k == 0 ? isWriteProbabilitiesInFiles : false);
                }
                result = algorithm.getPopulation().getBestIndividual().getObjectiveFunctionValue();
            } else if (selfConfigurationType == 2) {
                ChainedAlgorithm chainedAlgorithm = new ChainedAlgorithm();
                chainedAlgorithm.init("problems\\problem" + problemNumber + ".txt",
                        "settings\\settings" + problemNumber + ".txt");
                chainedAlgorithm.initOperatorSettings("operators");
                if (isUseElitism) {
                    chainedAlgorithm.process(problemNumber, k == 0 ? isWriteProbabilitiesInFiles : false, true);
                } else {
                    chainedAlgorithm.process(problemNumber, k == 0 ? isWriteProbabilitiesInFiles : false);
                }
                result = chainedAlgorithm.getPopulation().getBestIndividual().getObjectiveFunctionValue();
            }
            sum += result;
            results.add(result.toString());
            if (result < min) {
                min = result;
            }
        }
        results.add("best: " + min);
        results.add("average: " + sum / runsAmount);
        Files.write(Paths.get("results\\result-problem" + problemNumber + ".txt"), results);
    }

    private static void readParams(String[] args) {
        if (args.length > 0) {
            if (!"".equals(args[0])) {
                selfConfigurationType = Integer.parseInt(args[0]);
            }
        }
        if (args.length > 1) {
            if ("1".equals(args[1])) {
                isWriteProbabilitiesInFiles = true;
            }
        }
        if (args.length > 2) {
            if (!"1".equals(args[2])) {
                isUseElitism = false;
            }
        }
    }

    public static void main(String[] args) {
        readParams(args);
        try {
            if (selfConfigurationType == 1 || selfConfigurationType == 2) {
                for (int i = 0; i < 3; i++) {
                    int finalI = i;
                    new Thread(() -> {
                        try {
                            runProblem(finalI);
                        } catch (IOException e) {
                            logger.error(e.getMessage());
                            e.printStackTrace();
                        }
                    }).start();
                }
            } else {
                Map<OperatorType, Map<OperatorSettings, Operator>> allOperators = new HashMap<>();
                Operator.initOperatorSettings(allOperators, "operators");
                for (int i = 0; i < 3; i++) {
                    int finalI = i;
                    new Thread(() -> {
                        try {
                            runProblem(allOperators, finalI);
                        } catch (IOException e) {
                            logger.error(e.getMessage());
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
