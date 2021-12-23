package com.haidv.lab.ecommerce.dto.perfume;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@Data
@XmlRootElement(name = "perfume")
@XmlType(propOrder = {"perfumers", "genders", "prices", "sortByPrice", "perfumer", "perfumeGender"})
public class PerfumeSearchRequestDto {
    private List<String> perfumers;
    private List<String> genders;
    private List<Integer> prices;
    private boolean sortByPrice;
    private String perfumer;
    private String perfumeGender;
}
