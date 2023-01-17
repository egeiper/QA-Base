package org.egeiper;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;

public final class ParameterStoreUtils {
    private static final AWSSimpleSystemsManagement SSM_CLIENT = AWSSimpleSystemsManagementClientBuilder
            .standard().withRegion(PropertyUtils.getProperty("config.properties", "awsRegion")).build();

    private ParameterStoreUtils() {
    }


    public static String get(final String parameterKey) {
        final GetParameterRequest paramRequest = new GetParameterRequest()
                .withName(parameterKey).withWithDecryption(Boolean.TRUE);
        return SSM_CLIENT.getParameter(paramRequest).getParameter().getValue();
    }


}
