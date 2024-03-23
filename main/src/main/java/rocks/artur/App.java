package rocks.artur;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.util.Arrays;


@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }

    @Bean
    BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return beanFactory -> genericApplicationContext((BeanDefinitionRegistry) beanFactory);
    }

    void genericApplicationContext(BeanDefinitionRegistry beanRegistry) {
        ClassPathBeanDefinitionScanner beanDefinitionScanner = new ClassPathBeanDefinitionScanner(beanRegistry);
        String profile = System.getProperty("spring.profiles.active", "unknown");
        System.out.println(profile);
        beanDefinitionScanner.addIncludeFilter(removeModelAndEntitiesFilter());
        String[] packages;
        switch (profile) {
            case "clickhouse":
                packages = new String[]{"rocks.artur.api", "rocks.artur.api_impl", "rocks.artur.FITSClient", "rocks.artur.endpoints.RestService", "rocks.artur.clickhouse"};
                break;

            default:
                packages = new String[]{"rocks.artur.api", "rocks.artur.api_impl", "rocks.artur.FITSClient", "rocks.artur.endpoints.RestService", "rocks.artur.jpa"};
                break;
        }
        beanDefinitionScanner.scan(packages);
    }

    TypeFilter removeModelAndEntitiesFilter() {
        return (MetadataReader mr, MetadataReaderFactory mrf) -> {
            System.out.println(mr.getClassMetadata()
                    .getClassName());
            return !mr.getClassMetadata()
                    .getClassName()
                    .startsWith("rocks.artur.domain") &&
                    !mr.getClassMetadata()
                            .getClassName()
                            .startsWith("rocks.artur.api_impl.filter") &&
                    !mr.getClassMetadata()
                            .getClassName()
                            .startsWith("rocks.artur.api_impl.utils");
            };
    }
}