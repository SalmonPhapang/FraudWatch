
package com.fraudwatch.fraudruleengine.service;

import com.fraudwatch.fraudruleengine.dto.FraudCheckRequest;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Map;

@Service
public class SanitizerService {

    private static final PolicyFactory POLICY = Sanitizers.FORMATTING.and(Sanitizers.BLOCKS);

    public void sanitize(FraudCheckRequest request) {
        // Sanitize all string fields in the request and nested objects
        sanitizeObject(request);

        // Sanitize nested objects
        if (request.getCustomer() != null) {
            sanitizeObject(request.getCustomer());
        }
        if (request.getDevice() != null) {
            sanitizeObject(request.getDevice());
        }
        if (request.getMerchant() != null) {
            sanitizeObject(request.getMerchant());
        }
        if (request.getPayment() != null) {
            sanitizeObject(request.getPayment());
        }
        if (request.getAddress() != null) {
            sanitizeObject(request.getAddress());
        }
        if (request.getLocation() != null) {
            sanitizeObject(request.getLocation());
        }
        if (request.getAuthentication() != null) {
            sanitizeObject(request.getAuthentication());
        }
        if (request.getMetadata() != null) {
            sanitizeMap(request.getMetadata());
        }
    }

    private void sanitizeObject(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value instanceof String) {
                    String sanitized = POLICY.sanitize((String) value);
                    field.set(obj, sanitized);
                }
            } catch (IllegalAccessException e) {
                // Ignore
            }
        }
    }

    private void sanitizeMap(Map<String, Object> map) {
        map.replaceAll((k, v) -> {
            if (v instanceof String) {
                return POLICY.sanitize((String) v);
            }
            return v;
        });
    }
}

