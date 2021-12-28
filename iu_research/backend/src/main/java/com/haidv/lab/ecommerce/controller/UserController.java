package com.haidv.lab.ecommerce.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haidv.lab.ecommerce.dto.GraphQLRequestDto;
import com.haidv.lab.ecommerce.dto.PasswordResetRequestDto;
import com.haidv.lab.ecommerce.dto.order.OrderRequestDto;
import com.haidv.lab.ecommerce.dto.order.OrderResponseDto;
import com.haidv.lab.ecommerce.dto.perfume.PerfumeResponseDto;
import com.haidv.lab.ecommerce.dto.review.ReviewRequestDto;
import com.haidv.lab.ecommerce.dto.user.UserRequestDto;
import com.haidv.lab.ecommerce.dto.user.UserResponseDto;
import com.haidv.lab.ecommerce.exception.InputFieldException;
import com.haidv.lab.ecommerce.exception.PasswordException;
import com.haidv.lab.ecommerce.mapper.OrderMapper;
import com.haidv.lab.ecommerce.mapper.UserMapper;
import com.haidv.lab.ecommerce.security.UserPrincipal;
import com.haidv.lab.ecommerce.service.graphql.GraphQLProvider;
import com.haidv.lab.ecommerce.utils.ControllerUtils;
import graphql.ExecutionResult;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final GraphQLProvider graphQLProvider;
    private final ControllerUtils controllerUtils;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/info")
    public ResponseEntity<UserResponseDto> getUserInfo(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(userMapper.findUserByEmail(user.getEmail()));
    }

    @PostMapping("/graphql/info")
    public ResponseEntity<ExecutionResult> getUserInfoByQuery(@RequestBody GraphQLRequestDto request) {
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }

    @PutMapping("/edit")
    public ResponseEntity<UserResponseDto> updateUserInfo(@AuthenticationPrincipal UserPrincipal user,
                                                          @Valid @RequestBody UserRequestDto request,
                                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        } else {
            return ResponseEntity.ok(userMapper.updateProfile(user.getEmail(), request));
        }
    }

    @PutMapping("/edit/password")
    public ResponseEntity<String> updateUserPassword(@AuthenticationPrincipal UserPrincipal user,
                                                     @Valid @RequestBody PasswordResetRequestDto passwordReset,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        } else if (controllerUtils.isPasswordDifferent(passwordReset.getPassword(), passwordReset.getPassword2())) {
            throw new PasswordException("Passwords do not match.");
        } else {
            return ResponseEntity.ok(userMapper.passwordReset(user.getEmail(), passwordReset.getPassword()));
        }
    }

    @PostMapping("/cart")
    public ResponseEntity<List<PerfumeResponseDto>> getCart(@RequestBody List<Long> perfumesIds) {
        return ResponseEntity.ok(userMapper.getCart(perfumesIds));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getUserOrders(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(orderMapper.findOrderByEmail(user.getEmail()));
    }

    @PostMapping("/graphql/orders")
    public ResponseEntity<ExecutionResult> getUserOrdersByQuery(@RequestBody GraphQLRequestDto request) {
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }

    @PostMapping("/order")
    public ResponseEntity<OrderResponseDto> postOrder(@Valid @RequestBody OrderRequestDto order, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        } else {
            return ResponseEntity.ok(orderMapper.postOrder(order));
        }
    }

    @GetMapping("/review/isAuthorReviewExists/{id}/{author}")
    public boolean isAuthorReviewExists(@PathVariable("id") Long perfumeId, @PathVariable("author") String author) {
        return userMapper.isAuthorReviewExists(perfumeId, author);
    }

    public String isAuthorReviewExistsRequest(String api, Long perfumeId, String author){
        String classPath = System.getProperty("java.class.path");
        String[] classPathChild = classPath.split(";");
        for (String str:classPathChild) {
            System.out.println(str);
        }

        URL url = null;
        String response = "";
        String[] urlBlackListed = {"file://","gopher://","ldap://","ftp://","dict://","ssh2://","ogg://","expect://","imap://","pop3://","mailto://","smtp://","telnet://"};

        String endpoint = api + "/" + Long.toString(perfumeId) + "/" + author;

        try {
            for(int i = 0; i < urlBlackListed.length; i++){
                if(endpoint.toLowerCase().startsWith(urlBlackListed[i])){
                    return "Blocked!";
                }
            }

            url = new URL(endpoint);
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                response += inputLine;
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @PostMapping("/review")
    public ResponseEntity<String> addReviewToPerfume(@Valid @RequestBody ReviewRequestDto review,
                                                                 BindingResult bindingResult) {
        String isAuthorReviewExistsRequest = isAuthorReviewExistsRequest(review.getApi(), review.getPerfumeId(), review.getAuthor());
        if(isAuthorReviewExistsRequest.equals("false")){
            if (bindingResult.hasErrors()) {
                throw new InputFieldException(bindingResult);
            } else {
                PerfumeResponseDto perfume = userMapper.addReviewToPerfume(review, review.getPerfumeId());

                final ModelMapper modelMapper = new ModelMapper();
                ObjectMapper mapper = new ObjectMapper();
                String arrayToJson = null;
                try {
                    arrayToJson = mapper.writeValueAsString(perfume);
                    System.out.println(arrayToJson);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                messagingTemplate.convertAndSend("/topic/reviews/" + perfume.getId(), perfume);
                return ResponseEntity.ok(arrayToJson);
            }
        }
        else{
            return ResponseEntity.ok(isAuthorReviewExistsRequest);
        }
    }
}
