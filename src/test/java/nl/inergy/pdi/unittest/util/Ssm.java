package nl.inergy.pdi.unittest.util;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;

public class Ssm {

    private static AWSSimpleSystemsManagement ssm = AWSSimpleSystemsManagementClientBuilder.defaultClient();

    /**
     * Helper method to retrieve SSM Parameter's value
     *
     * @param parameterName identifier of the SSM Parameter
     * @return decrypted parameter value
     */
    public static String getParameter(String parameterName) {
        GetParameterRequest request = new GetParameterRequest();
        request.setName(parameterName);
        request.setWithDecryption(true);
        return ssm.getParameter(request).getParameter().getValue();
    }
}
