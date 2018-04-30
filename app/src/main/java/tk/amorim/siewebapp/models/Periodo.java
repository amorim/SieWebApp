package tk.amorim.siewebapp.models;

import java.util.ArrayList;

/**
 * Created by lucas on 09/07/2017.
 */

public class Periodo {
    private int ano;
    private String semestre;
    private ArrayList<Subject> subjects;

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<Subject> subjects) {
        this.subjects = subjects;
    }
}
