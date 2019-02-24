package com.ruubel.massfollow.service.http;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class HttpRequestService {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    public HttpResponse exchange(
            String url,
            Connection.Method method,
            HttpHeaders headers,
            Map<String, String> data
    ) {
        Connection.Response response = null;
        try {
            response = Jsoup
                    .connect(url)
                    .method(method)
                    .headers(headers.toSingleValueMap())
                    .data(data)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .execute();
        } catch (Exception e) {
            log.warn(String.format("Failed calling: '%s'", url));
        }
        return new HttpResponse(response.statusCode(), response.body());
    }

}
