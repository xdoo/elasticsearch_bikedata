package io.xdoo.rad.services;

import io.xdoo.rad.model.beans.DayCounterEntry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
public class StoreServiceTests {

    private final StoreService service = new StoreService(null);

    @Test
    public void testParseDateFromString() {
        LocalDate d01 = service.parseDateFromString("2019.10.11");
        assertThat(d01.getDayOfWeek(), is(equalTo(DayOfWeek.FRIDAY)));
        assertThat(d01.getMonth(), is(equalTo(Month.OCTOBER)));

        LocalDate d02 = service.parseDateFromString("11.10.2019");
        assertThat(d02.getDayOfWeek(), is(equalTo(DayOfWeek.FRIDAY)));
        assertThat(d02.getMonth(), is(equalTo(Month.OCTOBER)));
    }

    @Test
    public void testParseInt() {
        int r1 = service.parseInt("123");
        assertThat(r1, is(equalTo(123)));

        int r2 = service.parseInt("");
        assertThat(r2, is(equalTo(0)));

        int r3 = service.parseInt("drölf");
        assertThat(r3, is(equalTo(0)));
    }

    @Test
    public void testParseDouble() {
        double r1 = service.parseDouble("2.45");
        assertThat(r1, is(closeTo(2.45, 1)));

        double r2 = service.parseDouble("");
        assertThat(r2, is(closeTo(0.0, 0)));

        double r3 = service.parseDouble("drölf");
        assertThat(r3, is(closeTo(0.0, 0)));
    }

    @Test
    public void testCalculateTemp() {

        String r01 = service.calculateTemp(-15.0, -3.0);
        assertThat(r01, is(equalTo("sehr frostig")));

        String r02 = service.calculateTemp(-6.0, 2.0);
        assertThat(r02, is(equalTo("frostig")));

        String r03 = service.calculateTemp(6.0, 10.0);
        assertThat(r03, is(equalTo("kalt")));

        String r04 = service.calculateTemp(8.0, 18.0);
        assertThat(r04, is(equalTo("kühl")));

        String r05 = service.calculateTemp(17.0, 28.0);
        assertThat(r05, is(equalTo("warm")));

        String r06 = service.calculateTemp(25.0, 36.0);
        assertThat(r06, is(equalTo("heiß")));
    }

    @Test
    public void testCalculateWeather() {
        String r01 = service.calculateWeather(1.2, "sehr frostig");
        assertThat(r01, is(equalTo("leichter Schneefall")));

        String r02 = service.calculateWeather(7.8, "frostig");
        assertThat(r02, is(equalTo("Schneefall")));

        String r03 = service.calculateWeather(12.4, "frostig");
        assertThat(r03, is(equalTo("starker Schneefall")));

        String r04 = service.calculateWeather(1.6, "kühl");
        assertThat(r04, is(equalTo("leichter Regen")));

        String r05 = service.calculateWeather(5.7, "kühl");
        assertThat(r05, is(equalTo("Regen")));

        String r06 = service.calculateWeather(12.3, "kühl");
        assertThat(r06, is(equalTo("starker Regen")));

        String r07 = service.calculateWeather(0.0, "kühl");
        assertThat(r07, is(equalTo("trocken")));
    }

    @Test
    public void testMapRecords() throws IOException {
        List<DayCounterEntry> e01 = service.mapRecords(this.loadFile("/files/rad201701tage.csv"));
        assertThat(e01.isEmpty(), is(false));
        assertThat(e01.size(), is(equalTo(186)));

        List<DayCounterEntry> e02 = service.mapRecords(this.loadFile("/files/01record.csv"));
        assertThat(e02.isEmpty(), is(false));
        assertThat(e02.size(), is(equalTo(1)));
        assertThat(e02.get(0).getDayDate().toString(), is(equalTo("Sun Jan 01 00:00:00 CET 2017")));
        assertThat(e02.get(0).getCounter(), is(equalTo("Arnulf")));
        assertThat(e02.get(0).getTotalCount(), is(equalTo(135)));
        assertThat(e02.get(0).getMinTemp(), is(closeTo(-6.1, 0)));
        assertThat(e02.get(0).getMaxTemp(), is(closeTo(2.6, 0)));
        assertThat(e02.get(0).getRain(), is(closeTo(0.0, 0)));
        assertThat(e02.get(0).getClouds(), is(equalTo(36)));
        assertThat(e02.get(0).getSun(), is(closeTo(5.9, 0)));
        assertThat(e02.get(0).getMonth(), is(equalTo("JANUARY")));
        assertThat(e02.get(0).getWeekDay(), is(equalTo("SUNDAY")));
        assertThat(e02.get(0).getWeather(), is(equalTo("trocken")));
        assertThat(e02.get(0).getTemperature(), is(equalTo("frostig")));
    }

    private InputStream loadFile(String path) {
        InputStream stream = this.getClass().getResourceAsStream(path);
        return stream;
    }

}
