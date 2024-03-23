package rocks.artur;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.util.Arrays;


@SpringBootApplication
public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }

    @Bean
    BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return beanFactory -> genericApplicationContext((BeanDefinitionRegistry) beanFactory);
    }
    void genericApplicationContext(BeanDefinitionRegistry beanRegistry) {
        ClassPathBeanDefinitionScanner beanDefinitionScanner = new ClassPathBeanDefinitionScanner(beanRegistry);
        String profile = System.getenv("DB_SELECTOR") == null ? System.getProperty("spring.profiles.active", "h2") : System.getenv("DB_SELECTOR");
        System.out.println(profile);
        beanDefinitionScanner.addIncludeFilter(App::match);
        String[] packages;
        switch (profile) {
            case "clickhouse" ->
                    packages = new String[]{"rocks.artur.api", "rocks.artur.api_impl", "rocks.artur.FITSClient", "rocks.artur.endpoints.RestService", "rocks.artur.clickhouse"};
            case "h2", "mysql" ->
                    packages = new String[]{"rocks.artur.api", "rocks.artur.api_impl", "rocks.artur.FITSClient", "rocks.artur.endpoints.RestService", "rocks.artur.jpa"};
            default ->
                    throw new UnsupportedOperationException("The selected db is not supported. Choose one from [clickhouse, mysql, h2]");
        }
        beanDefinitionScanner.scan(packages);
    }
    private static boolean match(MetadataReader mr, MetadataReaderFactory mrf) {
        String className = mr.getClassMetadata().getClassName();
        LOG.debug(className);
        String[] packagesToIgnore = new String[]{"rocks.artur.domain", "rocks.artur.api_impl.filter", "rocks.artur.api_impl.utils"};
        return Arrays.stream(packagesToIgnore).noneMatch(className::startsWith);
    }
}