package ci.doci.sygescom.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/accessDenied").setViewName("home");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        //Load file: validation.properties
        messageSource.setBasename("classpath:validation");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }


   /* @Bean
    Path path(){
        Path path = Paths.get(System.getenv("Pathweb")+ System.getProperty("file.separator") + "GCI");
        *//*path = Paths.get("C:" + System.getProperty("file.separator")
               x + "Users" + System.getProperty("file.separator") + "Public"
                + System.getProperty("file.separator") + "webservice"
                + System.getProperty("file.separator") + "garantie");
        log.info(path.toString());*//*
        return path;
}*/
}
