package tk.amorim.siewebapp.models;

/**
 * Created by lucas on 09/07/2017.
 */

public class Subject {
    private String code;
    private String name;
    private int ch;
    private String turma;
    private Avaliacao avaliacoes;
    private int faltas;
    private String status;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCh() {
        return ch;
    }

    public void setCh(int ch) {
        this.ch = ch;
    }

    public String getTurma() {
        return turma;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }

    public Avaliacao getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(Avaliacao avaliacoes) {
        this.avaliacoes = avaliacoes;
    }

    public int getFaltas() {
        return faltas;
    }

    public void setFaltas(int faltas) {
        this.faltas = faltas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
