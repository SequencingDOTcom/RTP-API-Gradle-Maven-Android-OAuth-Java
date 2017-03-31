package com.sequencing.androidoauth.core.importdata;

import com.sequencing.androidoauth.core.importdata.entities.AuthorizationBody;
import com.sequencing.androidoauth.core.importdata.entities.Import23andMeBody;
import com.sequencing.androidoauth.core.importdata.entities.ImportAncestry;
import com.sequencing.androidoauth.core.importdata.entities.ResponseAncestry;

import org.androidannotations.rest.spring.annotations.Accept;
import org.androidannotations.rest.spring.annotations.Body;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.RequiresHeader;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.api.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by omazurova on 12/28/2016.
 */

@Rest(rootUrl = "https://api.sequencing.com/", converters = GsonHttpMessageConverter.class)
public interface RestInterface {

    @Post("23andMe/Download")
    @RequiresHeader("Authorization")
    @Accept(MediaType.APPLICATION_JSON)
    Response23andMe authorization23andMe(@Body AuthorizationBody body);

    @Post("23andMe/DownloadWithSecurity")
    @RequiresHeader("Authorization")
    @Accept(MediaType.APPLICATION_JSON)
    Response23andMe startImport23andMe(@Body Import23andMeBody body);

    void setHeader(String name, String value);

    String getHeader(String name);

    RestTemplate getRestTemplate();

    @Post("Ancestry/Authorize")
    @RequiresHeader("Authorization")
    @Accept(MediaType.APPLICATION_JSON)
    ResponseAncestry ancestryAuthorization(@Body AuthorizationBody body);

    @Post("Ancestry/Download")
    @RequiresHeader("Authorization")
    @Accept(MediaType.APPLICATION_JSON)
    ResponseAncestry startAncestryImport(@Body ImportAncestry body);
}
