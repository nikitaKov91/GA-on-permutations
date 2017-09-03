package settings;

import util.MutationProbability;
import util.MutationType;

import java.util.Arrays;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class MutationSettings {

    private MutationType mutationType;
    private MutationProbability mutationProbability;

    public void init(String[] params) {
        try {
            mutationType = MutationType.valueOf(params[0]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Передан неверный тип мутации: " + params[0] +
                    " допустимые значения: " + Arrays.toString(MutationType.values()));
        }
        if (mutationType != MutationType.TYPICAL) {
            try {
                mutationProbability = MutationProbability.valueOf(params[1]);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Передана неверная вероятность мутации: " + params[1] +
                        " допустимые значения: " + Arrays.toString(MutationProbability.values()));
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Тип мутации: ");
        switch (mutationType) {
            case TYPICAL:
                sb.append("стандартная");
                break;
            case CUSTOM:
                sb.append("кастомная");
                sb.append(". Вероятность мутации: ");
                switch (mutationProbability) {
                    case LOW:
                        sb.append("низкая");
                        break;
                    case AVERAGE:
                        sb.append("средняя");
                        break;
                    case HIGH:
                        sb.append("высокая");
                        break;
                }
                break;
        }
        return sb.toString();
    }

    public MutationProbability getMutationProbability() {
        return mutationProbability;
    }

    public void setMutationProbability(MutationProbability mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public MutationType getMutationType() {
        return mutationType;
    }

    public void setMutationType(MutationType mutationType) {
        this.mutationType = mutationType;
    }

}
