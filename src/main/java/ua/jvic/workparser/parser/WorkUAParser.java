package ua.jvic.workparser.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ua.jvic.workparser.model.Vacancy;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class WorkUAParser implements Parser {
    private final static String VACANCY_BLOCK = ".card-hover.job-link";
    private final static String SEARCH_URL = "https://www.work.ua/jobs-java/?page=%d";
    private final static String URL = "https://work.ua";
    private final static String TITLE = "h2 a";
    private final static String CITY = "div span:nth-child(3)";
    private final static String COST = ".f-vacancylist-characs-block p.-price";
    private final static String COMPANY_NAME = "div span";
    private final static String LINK = "h2 a";
    private final static String COUNT_VACANCIES = "div.row h1";

    public List<Vacancy> getVacancies() {
        int currentPage = 1;
        int currentCount = 0;
        int countVacancies = 1;
        SocketAddress address = new InetSocketAddress("proxy.krruda.dp.ua", 3128);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
        HttpsURLConnection connection = null;
        List<Vacancy> list = null;
        while (currentCount < countVacancies) {
            try {
                URL url = new URL(String.format(SEARCH_URL, currentPage));
                connection = (HttpsURLConnection) url.openConnection(proxy);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            Document doc;
            try {
                doc = Jsoup.parse(connection.getInputStream(), "UTF-8", SEARCH_URL);
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            Elements vacancies = doc.select(VACANCY_BLOCK);

            if (countVacancies == 1) {
                Scanner in = new Scanner(doc.select(COUNT_VACANCIES).text()).useDelimiter("[^0-9]+");
                countVacancies = in.nextInt();
            }
            if (list == null)
                list = new ArrayList<>(countVacancies);

            for (Element vacancy : vacancies) {
                currentCount++;
                String title, company, link, city, cost;
                title = vacancy.select(TITLE).text();
                company = vacancy.select(COMPANY_NAME).get(0).text();
                city = vacancy.select(CITY).text();
                link = URL + vacancy.select(LINK).attr("href");
                cost = vacancy.select(COST).text();
                Vacancy v = new Vacancy(title, company, link);
                v.setCost(cost);
                if (list.contains(v)) {
                    list.get(list.indexOf(v)).addCity(city);
                } else {
                    v.addCity(city);
                    list.add(v);
                }
            }
            currentPage++;
        }
        return list;
    }
}
