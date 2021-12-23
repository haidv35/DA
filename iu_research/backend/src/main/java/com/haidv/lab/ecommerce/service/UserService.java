package com.haidv.lab.ecommerce.service;

import com.haidv.lab.ecommerce.domain.Perfume;
import com.haidv.lab.ecommerce.domain.Review;
import com.haidv.lab.ecommerce.domain.User;
import com.haidv.lab.ecommerce.security.oauth2.OAuth2UserInfo;
import graphql.schema.DataFetcher;

import java.util.List;
import java.util.Map;

public interface UserService {

    User findUserById(Long userId);

    User findUserByEmail(String email);

    DataFetcher<List<User>> getAllUsersByQuery();

    DataFetcher<User> getUserByQuery();

    List<User> findAllUsers();

    User findByPasswordResetCode(String code);

    List<Perfume> getCart(List<Long> perfumeIds);

    Map<String, Object> login(String email);

    boolean registerUser(User user);

    User registerOauth2User(String provider, OAuth2UserInfo oAuth2UserInfo);

    User updateOauth2User(User user, String provider, OAuth2UserInfo oAuth2UserInfo);

    boolean activateUser(String code);

    boolean sendPasswordResetCode(String email);

    String passwordReset(String email, String password);

    void userSave(String username, Map<String, String> form, User user);

    User updateProfile(String email, User user);

    boolean isAuthorReviewExists(Long perfumeId, String author);

    Perfume addReviewToPerfume(Review review, Long perfumeId);
}
