package rocks.artur;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableCaching
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
        beanDefinitionScanner.addIncludeFilter(removeModelAndEntitiesFilter());
        beanDefinitionScanner.scan("rocks.artur.api","rocks.artur.api_impl","rocks.artur.FITSClient","rocks.artur.jpa","rocks.artur.endpoints.RestService");
    }

    static TypeFilter removeModelAndEntitiesFilter() {
        return (MetadataReader mr, MetadataReaderFactory mrf) -> {
            return !mr.getClassMetadata()
                    .getClassName()
                    .startsWith("rocks.artur.domain") &&
                    !mr.getClassMetadata()
                    .getClassName()
                    .startsWith("rocks.artur.api_impl.filter") &&
                    !mr.getClassMetadata()
                    .getClassName()
                    .startsWith("rocks.artur.api_impl.utils")
                    ;
        };
    }
}