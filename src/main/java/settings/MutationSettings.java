package settings;

import util.MutationProbabilityType;
import util.MutationType;

import java.util.Arrays;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class MutationSettings {

    private MutationType mutationType;
    private MutationProbabilityType mutationProbabilityType;
    private Double mutationProbability;

    public void init(String[] params) {
        try {
            mutationType = MutationType.valueOf(params[0]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Передан неверный тип мутации: " + params[0] +
                    " допустимые значения: " + Arrays.toString(MutationType.values()));
        }
        if (mutationType == MutationType.TYPICAL) {
            mutationProbability = Double.valueOf(params[1]);
        } else if (mutationType == MutationType.CUSTOM) {
            try {
                mutationProbabilityType = MutationProbabilityType.valueOf(params[1]);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Передан неверный тип вероятности мутации: " + params[1] +
                        " допустимые значения: " + Arrays.toString(MutationProbabilityType.values()));
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Тип мутации: ");
        switch (mutationType) {
            case TYPICAL:
                sb.append("стандартная. В");
                break;
            case CUSTOM:
                sb.append("кастомная. ");
                switch (mutationProbabilityType) {
                    case VERY_LOW:
                        sb.append("Очень низкая");
                        break;
                    case LOW:
                        sb.append("Низкая");
                        break;
                    case AVERAGE:
                        sb.append("Средняя");
                        break;
                    case HIGH:
                        sb.append("Высокая");
                        break;
                    case VERY_HIGH:
                        sb.append("Очень высокая");
                        break;
                }
                sb.append(" в");
                break;
        }
        sb.append("ероятность мутации: " + mutationProbability);
        return sb.toString();
    }

    public Double getMutationProbability() {
        return mutationProbability;
    }

    public void setMutationProbability(Double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public MutationProbabilityType getMutationProbabilityType() {
        return mutationProbabilityType;
    }

    public void setMutationProbabilityType(MutationProbabilityType mutationProbabilityType) {
        this.mutationProbabilityType = mutationProbabilityType;
    }

    public MutationType getMutationType() {
        return mutationType;
    }

    public void setMutationType(MutationType mutationType) {
        this.mutationType = mutationType;
    }

}
