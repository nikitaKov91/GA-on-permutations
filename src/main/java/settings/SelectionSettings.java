package settings;

import util.SelectionType;

import java.util.Arrays;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class SelectionSettings {

    private SelectionType selectionType;
    private Integer tournamentSize;

    public void init(String[] params) {
        try {
            selectionType = SelectionType.valueOf(params[0]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Передан неверный тип селекции: " + params[0] +
                    " допустимые значения: " + Arrays.toString(SelectionType.values()));
        }
        switch (selectionType) {
            case PROPORTIONAL:
                break;
            case RANKING:
                break;
            case TOURNAMENT:
                tournamentSize = Integer.parseInt(params[1]);
                break;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Тип селекции: ");
        switch (selectionType) {
            case PROPORTIONAL:
                sb.append("пропорциональная");
                break;
            case RANKING:
                sb.append("ранговая");
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

}
