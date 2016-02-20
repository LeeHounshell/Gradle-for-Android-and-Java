/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.harlie.builditbigger.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.youmustbejoking.JokeProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;

/** An endpoint class we are exposing */
@Api(
  name = "myApi",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "backend.builditbigger.harlie.com",
    ownerName = "backend.builditbigger.harlie.com",
    packagePath=""
  )
)
public class MyEndpoint {
    private static final Logger log = Logger.getLogger(MyEndpoint.class.getName());

    /** A simple endpoint method that takes a name and says Hi back */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name) {
        log.log(Level.INFO, "LEE: sayHi - name="+name);
        MyBean response = new MyBean();
        response.setData("Hi, " + name);
        return response;
    }

    @ApiMethod(name = "getJoke")
    public MyBean getJoke(@Named("defaultJoke") String defaultJoke, @Named("firstname") String firstname, @Named("lastname") String lastname) {
        MyBean response = new MyBean();
        String joke = JokeProvider.getJoke(defaultJoke, firstname, lastname, false);
        log.log(Level.INFO, "LEE: getJoke - joke="+joke);
        response.setData(joke);
        return response;
    }

}
