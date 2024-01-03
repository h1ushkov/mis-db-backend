package com.examin.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "parts")
public class PartsDTO {

    @Id
    private String id;

    @JsonProperty("OldCode")
    private String OldCode;

    @JsonProperty("P1P2P3")
    private String P1P2P3;

    @JsonProperty("FinishAndProducer")
    private String FinishAndProducer;

    @JsonProperty("Model")
    private String Model;

    @JsonProperty("Name")
    private String Name;

    @JsonProperty("Price")
    private Float Price;

    @JsonProperty("Availability")
    private String Availability;

    public PartsDTO(String OldCode, String P1P2P3, String FinishAndProducer, String Model, String Name, Float Price, String Availability) {
        this.id = new ObjectId().toString();
        this.OldCode = OldCode;
        this.P1P2P3 = P1P2P3;
        this.FinishAndProducer = FinishAndProducer;
        this.Model = Model;
        this.Name = Name;
        this.Price = Price;
        this.Availability = Availability;
    }
}
