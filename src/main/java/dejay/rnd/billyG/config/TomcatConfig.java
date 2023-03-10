package dejay.rnd.billyG.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class TomcatConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        //spring boot path mapping... feat.mac
        /*registry.addResourceHandler("/image/**")
                .addResourceLocations("file:///Users/mac/Documents/");*/

        String osName = System.getProperty("os.name").toLowerCase();
        log.info("os_info :: {} ", osName);
        if (osName.contains("win")) {
            registry.addResourceHandler("/image/**")
                    .addResourceLocations("file:///C:/home/ubuntu/research/image/");
        } else if (osName.contains("mac")){
            System.out.println("TomcatConfig.addResourceHandlers.elseIf");
            registry.addResourceHandler("/image/**")
                    .addResourceLocations("file:/Users/hongjin-yeong/Desktop/home/image/");
        } else {
            System.out.println("TomcatConfig.addResourceHandlers.else");
            registry.addResourceHandler("/image/**")
                    .addResourceLocations("file:/home/image/");
        }
    }

}

