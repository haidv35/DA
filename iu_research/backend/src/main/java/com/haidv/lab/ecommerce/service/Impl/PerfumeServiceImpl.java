package com.haidv.lab.ecommerce.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.haidv.lab.ecommerce.domain.Perfume;
import com.haidv.lab.ecommerce.dto.perfume.PerfumeResponseDto;
import com.haidv.lab.ecommerce.repository.PerfumeRepository;
import com.haidv.lab.ecommerce.repository.ReviewRepository;
import com.haidv.lab.ecommerce.service.PerfumeService;
import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerfumeServiceImpl implements PerfumeService {

    private final PerfumeRepository perfumeRepository;
    private final ReviewRepository reviewRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public DataFetcher<Perfume> getPerfumeByQuery() {
        return dataFetchingEnvironment -> {
            Long perfumeId = Long.parseLong(dataFetchingEnvironment.getArgument("id"));
            return perfumeRepository.findById(perfumeId).get();
        };
    }

    @Override
    public DataFetcher<List<Perfume>> getAllPerfumesByQuery() {
        return dataFetchingEnvironment -> perfumeRepository.findAllByOrderByIdAsc();
    }

    @Override
    public DataFetcher<List<Perfume>> getAllPerfumesByIdsQuery() {
        return dataFetchingEnvironment -> {
            List<String> objects = dataFetchingEnvironment.getArgument("ids");
            List<Long> perfumesId = objects.stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            return perfumeRepository.findByIdIn(perfumesId);
        };
    }

    @Override
    public Perfume findPerfumeById(Long perfumeId) {
        return perfumeRepository.findById(perfumeId).get();
    }

    @Override
    public List<Perfume> findAllPerfumes() {
        return perfumeRepository.findAllByOrderByIdAsc();
    }

    @Override
    public List<Perfume> findPerfumesByIds(List<Long> perfumesId) {
        return perfumeRepository.findByIdIn(perfumesId);
    }

    @Override
    public List<Perfume> filter(List<String> perfumers, List<String> genders, List<Integer> prices, boolean sortByPrice) {
        List<Perfume> perfumeList = new ArrayList<>();

        if (!perfumers.isEmpty() || !genders.isEmpty() || !prices.isEmpty()) {
            if (!perfumers.isEmpty()) {
                if (!perfumeList.isEmpty()) {
                    List<Perfume> perfumersList = new ArrayList<>();
                    for (String perfumer : perfumers) {
                        perfumersList.addAll(perfumeList.stream()
                                .filter(perfume -> perfume.getPerfumer().equals(perfumer))
                                .collect(Collectors.toList()));
                    }
                    perfumeList = perfumersList;
                } else {
                    perfumeList.addAll(perfumeRepository.findByPerfumerIn(perfumers));
                }
            }
            if (!genders.isEmpty()) {
                if (!perfumeList.isEmpty()) {
                    List<Perfume> gendersList = new ArrayList<>();
                    for (String gender : genders) {
                        gendersList.addAll(perfumeList.stream()
                                .filter(perfume -> perfume.getPerfumeGender().equals(gender))
                                .collect(Collectors.toList()));
                    }
                    perfumeList = gendersList;
                } else {
                    perfumeList.addAll(perfumeRepository.findByPerfumeGenderIn(genders));
                }
            }
            if (!prices.isEmpty()) {
                perfumeList = perfumeRepository.findByPriceBetween(prices.get(0), prices.get(1));
            }
        } else {
            perfumeList = perfumeRepository.findAllByOrderByIdAsc();
        }
        if (sortByPrice) {
            perfumeList.sort(Comparator.comparing(Perfume::getPrice));
        } else {
            perfumeList.sort((perfume1, perfume2) -> perfume2.getPrice().compareTo(perfume1.getPrice()));
        }
        return perfumeList;
    }

    @Override
    public String filterXML(String filter) {
        List<Perfume> perfumeList = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.isIgnoringElementContentWhitespace();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(filter));
            Document doc = builder.parse(is);

            System.out.println(doc);

            List<String> perfumers = new ArrayList<>();
            List<String> genders = new ArrayList<>();
            List<Integer> prices = new ArrayList<>();
            boolean sortByPrice = false;

            String perfumersData = doc.getElementsByTagName("perfumers").item(0).getTextContent();
            String gendersData = doc.getElementsByTagName("genders").item(0).getTextContent();
            String pricesData = doc.getElementsByTagName("prices").item(0).getTextContent();

            System.out.println(perfumersData);

            if(!perfumersData.equals("")){
                perfumers.add(doc.getElementsByTagName("perfumers").item(0).getTextContent());
            }

            if(!gendersData.equals("")){
                genders.add(doc.getElementsByTagName("genders").item(0).getTextContent());
            }
            if(pricesData.equals("")){
                String[] priceElement = pricesData.split(",");
                ArrayList<String> listOfPrices = new ArrayList<String>(Arrays.asList(priceElement));
                if(listOfPrices.size() == 2){
                    prices.add(Integer.parseInt(listOfPrices.get(0)));
                    prices.add(Integer.parseInt(listOfPrices.get(1)));
                }
            }

            if (!perfumers.isEmpty() || !genders.isEmpty() || !prices.isEmpty()) {
                if (!perfumers.isEmpty()) {
                    if (!perfumeList.isEmpty()) {
                        List<Perfume> perfumersList = new ArrayList<>();
                        for (String perfumer : perfumers) {
                            perfumersList.addAll(perfumeList.stream()
                                    .filter(perfume -> perfume.getPerfumer().equals(perfumer))
                                    .collect(Collectors.toList()));
                        }
                        perfumeList = perfumersList;
                    } else {
                        perfumeList.addAll(perfumeRepository.findByPerfumerIn(perfumers));
                    }
                }
                if (!genders.isEmpty()) {
                    if (!perfumeList.isEmpty()) {
                        List<Perfume> gendersList = new ArrayList<>();
                        for (String gender : genders) {
                            gendersList.addAll(perfumeList.stream()
                                    .filter(perfume -> perfume.getPerfumeGender().equals(gender))
                                    .collect(Collectors.toList()));
                        }
                        perfumeList = gendersList;
                    } else {
                        perfumeList.addAll(perfumeRepository.findByPerfumeGenderIn(genders));
                    }
                }
                if (!prices.isEmpty()) {
                    perfumeList = perfumeRepository.findByPriceBetween(prices.get(0), prices.get(1));
                }
            } else {
                perfumeList = perfumeRepository.findAllByOrderByIdAsc();
            }
            if (sortByPrice) {
                perfumeList.sort(Comparator.comparing(Perfume::getPrice));
            } else {
                perfumeList.sort((perfume1, perfume2) -> perfume2.getPrice().compareTo(perfume1.getPrice()));
            }
        }
        catch(Exception e){
            return e.getMessage();
        }


        final ModelMapper modelMapper = new ModelMapper();
        List<PerfumeResponseDto> responsePerfumeListDto = perfumeList.stream()
                .map((perfume) -> modelMapper.map(perfume, PerfumeResponseDto.class))
                .collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        String arrayToJson = null;
        try {
            arrayToJson = mapper.writeValueAsString(responsePerfumeListDto);
            System.out.println(arrayToJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return arrayToJson;
    }

    @Override
    public List<Perfume> findByPerfumerOrderByPriceDesc(String perfumer) {
        return perfumeRepository.findByPerfumerOrderByPriceDesc(perfumer);
    }

    @Override
    public List<Perfume> findByPerfumeGenderOrderByPriceDesc(String perfumeGender) {
        return perfumeRepository.findByPerfumeGenderOrderByPriceDesc(perfumeGender);
    }

    @Override
    public Perfume savePerfume(Perfume perfume, MultipartFile file) {
        if (file == null) {
            perfume.setFilename("empty.jpg");
        } else {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String resultFilename = "/" + file.getOriginalFilename();
            try {
                File finalFileString = new File(uploadPath, resultFilename);
                file.transferTo(finalFileString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            perfume.setFilename(resultFilename);
        }
        return perfumeRepository.save(perfume);
    }

    @Override
    @Transactional
    public List<Perfume> deletePerfume(Long perfumeId) {
        Perfume perfume = perfumeRepository.findById(perfumeId).get();
        perfume.getReviews().forEach(review -> reviewRepository.deleteById(review.getId()));
        perfumeRepository.delete(perfume);
        return perfumeRepository.findAllByOrderByIdAsc();
    }
}
