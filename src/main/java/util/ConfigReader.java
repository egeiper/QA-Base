package util;


import org.aeonbits.owner.Config;
@Config.Sources("classpath:selenium.properties")
public interface ConfigReader extends Config {

    @Key("seleniumGridHost")
    @DefaultValue("localhost")
    String gridHost();

    @Key("seleniumGridPort")
    @DefaultValue("4444")
    String gridPort();

    @Key("seleniumNodePort")
    @DefaultValue("5555")
    String nodePort();
}
