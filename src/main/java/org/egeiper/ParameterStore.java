package org.egeiper;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;

public class ParameterStore {

    private static final AWSSimpleSystemsManagement ssmClient = AWSSimpleSystemsManagementClientBuilder
            .standard().withRegion(PropertyUtils.getProperty("config.properties","awsRegion")).build();

    public static String get(String parameterKey) {
        GetParameterRequest paramRequest = new GetParameterRequest()
                .withName(parameterKey).withWithDecryption(Boolean.TRUE);
        return ssmClient.getParameter(paramRequest).getParameter().getValue();
    }


}
