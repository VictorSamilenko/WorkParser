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

@Component
public class RabotaUAParser implements Parser {
    private final static String VACANCY_BLOCK = ".f-vacancylist-vacancyblock";
    private final static String SEARCH_URL = "https://rabota.ua/jobsearch/vacancy_list?keyWords=java&pg=%d";
    private final static String URL = "https://rabota.ua";
    private final static String TITLE = ".f-visited-enable.ga_listing";
    private final static String CITY = ".f-vacancylist-characs-block p.fd-merchant";
    private final static String COST = ".f-vacancylist-characs-block p.-price";
    private final static String COMPANY_NAME = ".f-vacancylist-companyname";
    private final static String LINK = "h3.f-vacancylist-vacancytitle a";
    private final static String COUNT_VACANCIES = "#content_vacancyList_ltCount .fd-fat-merchant";

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
                doc = Jsoup.parse(connection.getInputStream(), "UTF-8", "");
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            Elements vacancies = doc.select(VACANCY_BLOCK);

            countVacancies = Integer.parseInt(doc.select(COUNT_VACANCIES).text());
            if (list == null)
                list = new ArrayList<>(countVacancies);

            for (Element vacancy : vacancies) {
                currentCount++;
                String title, company, link, city, cost;
                title = vacancy.select(TITLE).text();
                company = vacancy.select(COMPANY_NAME).text();
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
