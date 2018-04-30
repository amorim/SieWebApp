package tk.amorim.siewebapp.http;

import android.content.SharedPreferences;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import tk.amorim.siewebapp.models.Avaliacao;
import tk.amorim.siewebapp.models.Periodo;
import tk.amorim.siewebapp.models.Subject;

/**
 * Created by lucas on 09/07/2017.
 */

public class SieWebHttp {
    private static String token = "";
    public static boolean login(String cpf, String password) {
        StringWriter writer = new StringWriter();
        HttpURLConnection con = HttpRequest.get("https://sistemas.ufal.br/academico/login.seam").connectTimeout(10000).receive(writer).getConnection();
        Map<String, List<String>> header = con.getHeaderFields();
        List<String> cookiesHeader = header.get("Set-Cookie");
        token = HttpCookie.parse(cookiesHeader.get(0)).get(0).getValue();
        HashMap<String, String> params = new HashMap<>();
        params.put("loginForm", "loginForm");
        params.put("loginForm:username", cpf);
        params.put("loginForm:password", password);
        params.put("loginForm:rememberMe", "on");
        params.put("loginForm:entrar", "Entrar");
        params.put("javax.faces.ViewState", "j_id1");
        int code2 = HttpRequest.post("https://sistemas.ufal.br/academico/login.seam").connectTimeout(10000).header("Cookie", "JSESSIONID=" + token).form(params).code();
        int code3 = HttpRequest.get("https://sistemas.ufal.br/academico/home.seam").header("Cookie", "JSESSIONID=" + token).connectTimeout(10000).code();
        return code3 == 200;
    }

    public static ArrayList<Periodo> boletim(SharedPreferences sp) {
        if (token.isEmpty())
            login(sp.getString("cpf", ""), sp.getString("password", ""));
        HttpRequest req = HttpRequest.get("https://sistemas.ufal.br/academico/matricula/boletim.seam").header("Cookie", "JSESSIONID=" + token).connectTimeout(10000);
        int code = req.code();
        if (code != 200) {
            login(sp.getString("cpf", ""), sp.getString("password", ""));
            req = HttpRequest.get("https://sistemas.ufal.br/academico/matricula/boletim.seam").header("Cookie", "JSESSIONID=" + token).connectTimeout(10000);
            code = req.code();
        }
        if (code != 200) {
            return null;
        }
        String html = req.body();
        html = html.replace("\t", "").replace("\n", "");
        String[] tables = html.split("<br /><span style=\"font-weight: bold;\">");
        ArrayList<Periodo> periodos = new ArrayList<>();
        for (int i = 1; i < tables.length; i++) {
            Periodo periodo = new Periodo();
            try {
                periodo.setAno(Integer.parseInt(Html.fromHtml(getBetweenStrings(tables[i], "Per&iacute;odo: ", " /")).toString()));
            } catch (Exception ex) {
                periodo.setAno(0);
            }
            periodo.setSemestre(Html.fromHtml(getBetweenStrings(tables[i], "/ ", "</span>")).toString());
            String[] body = tables[i].split("<tbody");
            String[] linhas = body[1].split("<tr");
            ArrayList<Subject> subjects = new ArrayList<>();
            for (int j = 1; j < linhas.length; j++) {
                Subject subject = new Subject();
                Avaliacao aval = new Avaliacao();
                String[] colstemp = linhas[j].split("<td");
                ArrayList<String> temp = new ArrayList<>(Arrays.asList(colstemp));
                temp.remove(0);
                String[] cols = new String[temp.size()];
                cols = temp.toArray(cols);
                String codeandname = Html.fromHtml(getBetweenStrings(cols[0], ">", "<")).toString();
                String[] nameandcode = codeandname.split(" - ");
                subject.setCode(nameandcode[0]);
                subject.setName(nameandcode[1]);
                subject.setCh(Integer.parseInt(Html.fromHtml(getBetweenStrings(cols[1], ">", "<")).toString()));
                String faltas = getBetweenStrings(cols[8], ">", "<");
                if (!faltas.isEmpty())
                    subject.setFaltas(Integer.parseInt(faltas));
                else
                    subject.setFaltas(0);
                subject.setTurma(Html.fromHtml(getBetweenStrings(cols[2], ">", "<")).toString());
                subject.setStatus(Html.fromHtml(getBetweenStrings(cols[9], "\">", "</")).toString());
                String ab1 = getBetweenStrings(cols[3], ">", "<").replace(",", ".");
                try {
                    aval.setAb1(Double.parseDouble(ab1));
                } catch (Exception ex) {
                    aval.setAb1(-1);
                }
                String ab2 = getBetweenStrings(cols[4], ">", "<").replace(",", ".");
                try {
                    aval.setAb2(Double.parseDouble(ab2));
                } catch (Exception ex) {
                    aval.setAb2(-1);
                }
                String ra = getBetweenStrings(cols[5], ">", "<").replace(",", ".");
                try {
                    aval.setRa(Double.parseDouble(ra));
                } catch (Exception ex) {
                    aval.setRa(-1);
                }
                String pf = getBetweenStrings(cols[6], ">", "<").replace(",", ".");
                try {
                    aval.setPf(Double.parseDouble(pf));
                } catch (Exception ex) {
                    aval.setPf(-1);
                }
                String mf = getBetweenStrings(cols[7], ">", "<").replace(",", ".");
                try {
                    aval.setMf(Double.parseDouble(mf));
                } catch (Exception ex) {
                    aval.setMf(-1);
                }
                subject.setAvaliacoes(aval);
                subjects.add(subject);
            }
            periodo.setSubjects(subjects);
            periodos.add(periodo);
        }
        return periodos;
    }

    private static String getBetweenStrings(String text, String textFrom, String textTo) {
        String result = "";
        result = text.substring(text.indexOf(textFrom) + textFrom.length(), text.length());
        result = result.substring(0, result.indexOf(textTo));
        return result;
    }
}
