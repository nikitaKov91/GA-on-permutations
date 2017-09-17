package settings;

import util.MutationType;

import java.util.Arrays;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class MutationSettings {

    private MutationType mutationType;
    private Double mutationProbability;

    public void init(String[] params) {
        try {
            mutationType = MutationType.valueOf(params[0]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Передан неверный тип мутации: " + params[0] +
                    " допустимые значения: " + Arrays.toString(MutationType.values()));
        }
        if (mutationType != MutationType.TYPICAL) {
            mutationProbability = Double.valueOf(params[1]);
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
                sb.append("кастомная. Вероятность мутации: " + mutationProbability);
                break;
        }
        return sb.toString();
    }

    public Double getMutationProbability() {
        return mutationProbability;
    }

    public void setMutationProbability(Double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public MutationType getMutationType() {
        return mutationType;
    }

    public void setMutationType(MutationType mutationType) {
        this.mutationType = mutationType;
    }

}
