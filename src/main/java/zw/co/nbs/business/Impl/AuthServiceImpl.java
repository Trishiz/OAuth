package zw.co.nbs.business.Impl;

import lombok.extern.slf4j.Slf4j;
import zw.co.nbs.business.api.AuthService;
import zw.co.nbs.utils.Constants;
import zw.co.nbs.utils.EncodeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
public class AuthServiceImpl implements AuthService {

    private final RestTemplate restTemplate;
    private final Environment environment;

    @Value("${spring.security.oauth2.client.registration.finastra.scope}")
    private String scope;

    @Value("${spring.security.oauth2.client.provider.finastra.authorization-uri}")
    private String url;

    @Value("${spring.security.oauth2.client.registration.finastra.clientId}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.finastra.clientSecret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.finastra.authorization-grant-type}")
    private String grantType;


    public AuthServiceImpl(RestTemplate restTemplate, Environment environment) {
        this.restTemplate = restTemplate;
        this.environment = environment;
    }

    @Override
    @Cacheable(value = "authCache", key = "#root.method.name")
    public Map<String, Object> authenticate() {
        String username = environment.getRequiredProperty("auth.user.name");
        String password =  environment.getRequiredProperty("auth.user.password");

        String basicAuthorization = EncodeUtils.basicAuthorization(clientId, clientSecret);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE);
        requestHeaders.set(HttpHeaders.AUTHORIZATION, basicAuthorization);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.put(String.valueOf(Constants.SCOPE), Collections.singletonList(scope));
        requestBody.put(String.valueOf(Constants.USERNAME), Collections.singletonList(username));
        requestBody.put(String.valueOf(Constants.PASS_WORD), Collections.singletonList(password));
        requestBody.put(String.valueOf(Constants.GRANT_TYPE), Collections.singletonList(grantType));

        HttpEntity<Map> requestEntity = new HttpEntity<>(requestBody, requestHeaders);
        log.info(url, Locale.getDefault());
        try {

            ResponseEntity<Map> r = restTemplate.postForEntity(url, requestEntity, Map.class);

            if (r.getStatusCode() == HttpStatus.OK) {
                return r.getBody();
            }
        } catch (RestClientException e) {
            log.info(e.getLocalizedMessage(), Locale.getDefault());

        }
        return null;
    }
}
