package io.xdoo.rad.model.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Document(indexName = "bikecounter")
@Data
public class DayCounterEntry {

    @Id
    private String objectID;

    // Werte aus dem Dokument
    @Field(type = FieldType.Date)
    @JsonFormat (shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
    private Date dayDate;
    @Field(type = FieldType.Keyword)
    private String counter;
    @Field(type = FieldType.Integer)
    private int totalCount;
    @Field(type = FieldType.Double)
    private double minTemp;
    @Field(type = FieldType.Double)
    private double maxTemp;
    @Field(type = FieldType.Double)
    private double rain;
    @Field(type = FieldType.Integer)
    private int clouds;
    @Field(type = FieldType.Double)
    private double sun;

    // generierte Werte
    @Field(type = FieldType.Keyword)
    private String month;
    @Field(type = FieldType.Keyword)
    private String weekDay;
    @Field(type = FieldType.Keyword)
    private String weather;
    @Field(type = FieldType.Keyword)
    private String temperature;

}
