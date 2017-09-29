package ua.jvic.workparser.model;

import java.util.ArrayList;
import java.util.List;

public class Vacancy {
    private String title;
    private List<String> cities;
    private String company;
    private String link;
    private String cost;

    public Vacancy(String title, String company, String link) {
        this.title = title;
        this.company = company;
        this.link = link;
        cities = new ArrayList<String>();
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Vacancy{" +
                "title='" + title + '\'' +
                ", cities=" + cities +
                ", company='" + company + '\'' +
                ", link='" + link + '\'' +
                ", cost='" + cost + '\'' +
                '}'+'\n';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vacancy vacancy = (Vacancy) o;

        if (title != null ? !title.equals(vacancy.title) : vacancy.title != null) return false;
        return company != null ? company.equals(vacancy.company) : vacancy.company == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (company != null ? company.hashCode() : 0);
        return result;
    }

    public void addCity(String city) {
        cities.add(city);
    }
}
