package settings;

import util.RankingSelectionType;
import util.SelectionType;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class SelectionSettings {

    private SelectionType selectionType;
    // для турнирной селекции
    private Integer tournamentSize;
    // для ранговой селекции
    private RankingSelectionType rankingSelectionType;
    private Double weight;

    private SelectionSettings() {
    }

    public static SelectionSettings create() {
        return new SelectionSettings();
    }

    public SelectionSettings selectionType(SelectionType selectionType) {
        this.selectionType = selectionType;
        return this;
    }

    public SelectionSettings tournamentSize(Integer tournamentSize) {
        this.tournamentSize = tournamentSize;
        return this;
    }

    public SelectionSettings rankingSelectionType(RankingSelectionType rankingSelectionType) {
        this.rankingSelectionType = rankingSelectionType;
        return this;
    }

    public SelectionSettings weight(Double weight) {
        this.weight = weight;
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Тип селекции: ");
        switch (selectionType) {
            case PROPORTIONAL:
                sb.append("пропорциональная");
                break;
            case RANKING:
                sb.append("ранговая, ");
                switch (rankingSelectionType) {
                    case LINEAR:
                        sb.append("линейная");
                        break;
                    case EXPONENTIAL:
                        sb.append("экспоненциальная.");
                        sb.append(". Вес: " + weight);
                        break;
                }
                break;
            case TOURNAMENT:
                sb.append("турнирная");
                sb.append(". Размер турнира: " + tournamentSize);
                break;
        }
        return sb.toString();
    }

    public SelectionType getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(SelectionType selectionType) {
        this.selectionType = selectionType;
    }

    public Integer getTournamentSize() {
        return tournamentSize;
    }

    public void setTournamentSize(Integer tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    public RankingSelectionType getRankingSelectionType() {
        return rankingSelectionType;
    }

    public void setRankingSelectionType(RankingSelectionType rankingSelectionType) {
        this.rankingSelectionType = rankingSelectionType;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
