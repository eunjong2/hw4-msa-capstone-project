package hanwhadeliverysystemteam.common;

import hanwhadeliverysystemteam.ManagementApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = { ManagementApplication.class })
public class CucumberSpingConfiguration {}
