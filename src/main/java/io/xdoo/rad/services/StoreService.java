package io.xdoo.rad.services;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import io.xdoo.rad.model.beans.DayCounterEntry;
import io.xdoo.rad.model.repositories.DayCounterEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StoreService {

    private final DayCounterEntryRepository repository;

    public StoreService(DayCounterEntryRepository repository) {
        this.repository = repository;
    }

    public void indexFile(InputStream csv) {
        try {
            List<DayCounterEntry> records = this.mapRecords(csv);
            this.repository.saveAll(records);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<DayCounterEntry> mapRecords(InputStream csv) throws IOException {

        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withIgnoreEmptyLines()
                .withFirstRecordAsHeader()
                .parse(new InputStreamReader(csv));

        List<DayCounterEntry> result = new ArrayList<>();
        for (CSVRecord r : records) {
            DayCounterEntry entry = new DayCounterEntry();

            LocalDate localDate = this.parseDateFromString(r.get(0));
            entry.setDayDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            entry.setCounter((r.get(3)));
            entry.setTotalCount(this.parseInt(r.get(6)));
            entry.setMinTemp(this.parseDouble(r.get(7)));
            entry.setMaxTemp(this.parseDouble(r.get(8)));
            entry.setRain(this.parseDouble(r.get(9)));
            entry.setClouds(this.parseInt(r.get(10)));
            entry.setSun(this.parseDouble(r.get(11)));

            // errechnete Werte
            entry.setWeekDay(localDate.getDayOfWeek().toString());
            entry.setMonth(localDate.getMonth().toString());
            entry.setTemperature(this.calculateTemp(entry.getMinTemp(), entry.getMaxTemp()));
            entry.setWeather(this.calculateWeather(entry.getRain(), entry.getTemperature()));

            result.add(entry);

        }

        return result;
    }

    /**
     * Kategorisiert das Wetter.
     *
     * @param rain
     * @return
     */
    public String calculateWeather(double rain, String temp) {
        if(rain > 0 && (temp.equals("sehr frostig") || temp.equals("frostig"))) {
            if(rain > 0 && rain <= 2.0) {
                return "leichter Schneefall";
            } else if(rain > 2.0 && rain <= 10.0) {
                return "Schneefall";
            } else if(rain > 10.0) {
                return  "starker Schneefall";
            }
        } else if (rain > 0 && rain <= 2.0) {
            return "leichter Regen";
        } else if (rain > 2.0 && rain <= 10.0 ) {
            return "Regen";
        } else if(rain > 10.0) {
            return "starker Regen";
        }
        return "trocken";
    }

    /**
     * Kategoriesiert die Temperatur des Tages anhand
     * der Durchschnittstemperatur.
     *
     * @param min
     * @param max
     * @return
     */
    public String calculateTemp(double min, double max) {
        double t = (min + max) / 2;
        if(t < -5.0) {
            return "sehr frostig";
        } else if(t > -5.0 && t <= 0.0) {
            return "frostig";
        } else if(t > 0.0 && t <= 10.0) {
            return "kalt";
        } else if(t > 10.0 && t <= 20.0) {
            return "kühl";
        } else if(t > 20.0 && t <= 30.0) {
            return "warm";
        } else {
            return "heiß";
        }
    }

    /**
     * Parst ein LocalDate.
     *
     * @param d
     * @return
     */
    public LocalDate parseDateFromString(String d) {
        String result = "";
        if(d.contains(".")) {
            if(d.indexOf(".") > 3) {
                result = d.replaceAll("\\.", "-");
            } else {
                log.warn("wrong date format: {}", d);
                String[] p = d.split("\\.");
                StringBuilder builder = new StringBuilder();
                builder.append(p[2]).append("-").append(p[1]).append("-").append(p[0]);
                result = builder.toString();
            }
        }
        return LocalDate.parse(result);
    }

    /**
     * Parst ein int. Wenn die Eingabe nicht umgewandelt werden
     * kann, dann wird immer '0' zurück- und eine entsprechende
     * Warnung ausgegeben.
     *
     * @param i
     * @return
     */
    public int parseInt(String i) {
        @Nullable Integer r = Ints.tryParse(i);
        if(r == null) {
            log.warn("cannot parse '{}' to int value", i);
            return 0;
        } else {
            return r;
        }
    }

    /**
     * Parst ein double. Wenn die Eingabe nicht umgewandelt werden
     * kann, dann wird immer '0.0' zurück- und eine entsprechende
     * Warnung ausgegeben.
     *
     * @param d
     * @return
     */
    public double parseDouble(String d) {
        @Nullable Double r = Doubles.tryParse(d);
        if(r == null) {
            log.warn("cannot parse '{}' to double value", d);
            return 0.0;
        } else {
            return r;
        }

    }
}
