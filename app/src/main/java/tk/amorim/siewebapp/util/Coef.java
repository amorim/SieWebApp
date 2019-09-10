package tk.amorim.siewebapp.util;

import java.util.List;

import tk.amorim.siewebapp.models.Periodo;
import tk.amorim.siewebapp.models.Subject;

public class Coef {
    public static float calculate(List<Periodo> boletim) {
        double soma = 0;
        double qt = 0;
        for (Periodo p : boletim) {
            for (Subject s : p.getSubjects()) {
                if (s.getAvaliacoes().getMf() != -1) {
                    qt+=s.getCh();
                    soma += s.getCh() * s.getAvaliacoes().getMf();
                }
            }
        }
        Double coef = 0d;
        if (qt != 0) {
            coef = soma/qt;
        }
        return coef.floatValue();
    }
}
