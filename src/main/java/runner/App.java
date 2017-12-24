package runner;

import algorithm.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class App {

    static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        try {
            
            for (int i = 0; i <= 2; i++) {
                for (int j = 0; j <= 20; j++) {
                    List<String> result = new ArrayList<>();
                    for (int k = 0; k <= 99; k++) {
                        Algorithm algorithm = new Algorithm();
                        algorithm.init("C:\\Work\\projects\\GA-on-permutations\\problem" + i + ".txt",
                                "C:\\Work\\projects\\GA-on-permutations\\settings" + j + ".txt");
                        algorithm.process();
                        result.add(String.valueOf(algorithm.getPopulation().getBestIndividual().getObjectiveFunctionValue()));
                    }
                    StringBuilder sb = new StringBuilder("C:\\Work\\projects\\GA-on-permutations\\result-");
                    sb.append("problem" + i + "-settings" + j + ".txt");
                    Files.write(Paths.get(sb.toString()), result);
                }
            }

            /*
            Algorithm algorithm = new Algorithm();
            algorithm.init("C:\\Work\\projects\\GA-on-permutations\\problem0.txt",
                    "C:\\Work\\projects\\GA-on-permutations\\settings20.txt");
            algorithm.process();
            */
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
