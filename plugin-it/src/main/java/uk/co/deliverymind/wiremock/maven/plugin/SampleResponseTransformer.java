package uk.co.deliverymind.wiremock.maven.plugin;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;

public class SampleResponseTransformer extends ResponseTransformer {

    @Override
    public Response transform(Request request, Response response, FileSource files, Parameters parameters) {
        return Response.Builder.like(response).but()
                .headers(response.getHeaders()
                    .plus(new HttpHeader("SampleKey", "SampleValue")))
                .build();
    }

    public String getName() {
        return "sample-transformer";
    }
}
