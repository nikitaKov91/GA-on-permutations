package settings;

import util.MutationProbabilityType;
import util.MutationType;
import util.OperatorType;

import java.util.Arrays;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class MutationSettings implements OperatorSettings {

    private OperatorType operatorType = OperatorType.MUTATION;
    private MutationType mutationType;
    private MutationProbabilityType mutationProbabilityType;
    private Double mutationProbability;

    private MutationSettings() {
    }

    public static MutationSettings create() {
        return new MutationSettings();
    }

    public MutationSettings mutationType(MutationType mutationType) {
        this.mutationType = mutationType;
        return this;
    }

    public MutationSettings mutationProbabilityType(MutationProbabilityType mutationProbabilityType) {
        this.mutationProbabilityType = mutationProbabilityType;
        return this;
    }

    public MutationSettings mutationProbability(Double mutationProbability) {
        this.mutationProbability = mutationProbability;
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Тип мутации: ");
        switch (mutationType) {
            case BY_2_EXCHANGE:
                sb.append("стандартная. Вероятность мутации: " + mutationProbability);
                break;
            case BY_INVERSION:
                sb.append("мутация инверсией");
                break;
            case BY_INSERTION:
                sb.append("мутация вставкой");
                break;
            case BY_SHIFTING:
                sb.append("мутация переводом");
                break;
        }

        if (mutationType != MutationType.BY_2_EXCHANGE) {
            sb.append(". ");
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
            sb.append(" вероятность мутации");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MutationSettings that = (MutationSettings) o;

        if (mutationType != that.mutationType) return false;
        if (mutationProbabilityType != that.mutationProbabilityType) return false;
        return mutationProbability.equals(that.mutationProbability);
    }

    @Override
    public int hashCode() {
        int result = mutationType.hashCode();
        result = 31 * result + mutationProbabilityType.hashCode();
        result = 31 * result + mutationProbability.hashCode();
        return result;
    }

    public OperatorType getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(OperatorType operatorType) {
        this.operatorType = operatorType;
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
