package util;

import algorithm.Mutation;
import algorithm.Operator;
import algorithm.Recombination;
import algorithm.Selection;
import settings.MutationSettings;
import settings.OperatorSettings;
import settings.RecombinationSettings;
import settings.SelectionSettings;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Коваленко Никита on 15.01.2018.
 */
public class OperatorFactory {

    public static Operator createOperator(List<String> content) {
        try {
            OperatorType operatorType = OperatorType.valueOf(content.get(0));
            switch (operatorType) {
                case RECOMBINATION:
                    RecombinationSettings recombinationSettings = RecombinationSettings.create()
                            .init(content);
                    return new Recombination(recombinationSettings);
                case SELECTION:
                    SelectionSettings selectionSettings = SelectionSettings.create()
                            .init(content);
                    return new Selection(selectionSettings);
                case MUTATION:
                    MutationSettings mutationSettings = MutationSettings.create()
                            .init(content);
                    return new Mutation(mutationSettings);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Передан неверный тип оператора: " + content.get(0) +
                    " допустимые значения: " + Arrays.toString(OperatorType.values()));
        }
        return null;
    }

}
