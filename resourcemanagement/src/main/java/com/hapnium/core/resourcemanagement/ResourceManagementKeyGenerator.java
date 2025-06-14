package com.hapnium.core.resourcemanagement;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Generates keys for resource management operations such as caching or rate limiting.
 * Supports dynamic key construction using SpEL expressions, method arguments, and user/request context.
 *
 * <p>
 * This class is designed to be extended or configured to suit custom business needs.
 * It can include current user ID and IP address in the evaluation context for SpEL expressions.
 * The user information is supplied via a {@link UserResourceManagementProvider} implementation.
 * </p>
 *
 * <p>Typical usage includes:</p>
 * <ul>
 *     <li>Evaluating SpEL expressions to dynamically compute key values.</li>
 *     <li>Building default keys based on method and class names.</li>
 *     <li>Generating deterministic keys using method arguments and user/request metadata.</li>
 * </ul>
 *
 * @author Evaristus Adimonyemma
 * @since 1.0
 */
@Slf4j
public class ResourceManagementKeyGenerator {
    protected UserResourceManagementProvider userResourceManagementProvider;
    protected boolean addUserToContext;
    protected boolean addIpAddressToContext;
    protected String LOG_NAME;
    protected final ExpressionParser parser = new SpelExpressionParser();

    /**
     * Constructs a new instance of the key generator.
     *
     * @param addUserToContext whether to add the current user ID to the evaluation context
     * @param addIpAddressToContext whether to include the client's IP address in the context
     * @param LOG_NAME a log identifier used in logs
     * @param userResourceManagementProvider the provider used to resolve the current user
     */
    public ResourceManagementKeyGenerator(
            boolean addUserToContext,
            boolean addIpAddressToContext,
            String LOG_NAME,
            UserResourceManagementProvider userResourceManagementProvider
    ) {
        this.addUserToContext = addUserToContext;
        this.LOG_NAME = LOG_NAME;
        this.addIpAddressToContext = addIpAddressToContext;
        this.userResourceManagementProvider = userResourceManagementProvider;
    }

    /**
     * Returns the final key string from the keyBuilder.
     * If no parts are added, the default key is returned.
     *
     * @param keyBuilder the joiner with components of the key
     * @param defaultKey fallback key if none is built
     * @return final constructed key string
     */
    protected String buildKey(StringJoiner keyBuilder, String defaultKey) {
        String finalKey = keyBuilder.toString();
        
        // If no key parts were added, use the default
        if (finalKey.isEmpty()) {
            finalKey = defaultKey;
        }
        
        return finalKey;
    }

    /**
     * Adds common metadata (prefix, class, method) to the key builder.
     *
     * @param keyBuilder joiner for key parts
     * @param joinPoint AOP join point representing the method invocation
     * @param keyPrefix optional string prefix
     * @param includeClassName whether to include class name
     * @param includeMethodName whether to include method name
     */
    protected void addCommonKeyComponents(
            StringJoiner keyBuilder,
            ProceedingJoinPoint joinPoint,
            String keyPrefix,
            boolean includeClassName,
            boolean includeMethodName
    ) {
        if (!keyPrefix.isEmpty()) keyBuilder.add(keyPrefix);
        if (includeClassName) keyBuilder.add(joinPoint.getTarget().getClass().getSimpleName());
        if (includeMethodName) keyBuilder.add(joinPoint.getSignature().getName());
    }

    /**
     * Evaluates a SpEL expression against method context.
     *
     * @param expression the SpEL expression string
     * @param joinPoint the join point providing method context
     * @return the evaluated result as a String, or null if evaluation fails
     */
    protected @Nullable String evaluateSpelExpression(String expression, ProceedingJoinPoint joinPoint) {
        if (expression == null || expression.isEmpty()) {
            return null;
        }
        
        try {
            Expression exp = parser.parseExpression(expression);
            EvaluationContext context = createEvaluationContext(joinPoint);
            Object result = exp.getValue(context);
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            log.warn("{} Failed to evaluate SpEL expression: {}", LOG_NAME, expression, e);
            return null;
        }
    }

    /**
     * Creates an evaluation context for SpEL with arguments and optional user/IP metadata.
     *
     * @param joinPoint join point representing method call
     * @return a fully populated {@link EvaluationContext}
     */
    protected @NonNull EvaluationContext createEvaluationContext(@NonNull ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        
        // Add method arguments
        addMethodArguments(context, joinPoint);
        
        // Add method parameter names if available
        addParameterNames(context, joinPoint);
        
        // Add user context variables
        if(addUserToContext) {
            addUserContextVariables(context);
        }
        
        // Add request context variables
        if(addIpAddressToContext) {
            addRequestContextVariables(context);
        }
        
        return context;
    }

    /**
     * Adds method arguments to the evaluation context (arg0, arg1...).
     */
    private void addMethodArguments(StandardEvaluationContext context, ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                context.setVariable("arg" + i, args[i]);
                context.setVariable("p" + i, args[i]); // Alternative naming
            }
        }
    }

    /**
     * Adds named parameters to the evaluation context if available.
     */
    private void addParameterNames(StandardEvaluationContext context, ProceedingJoinPoint joinPoint) {
        try {
            Method method = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod();
            java.lang.reflect.Parameter[] parameters = method.getParameters();
            Object[] args = joinPoint.getArgs();
            
            if (parameters != null && args != null) {
                for (int i = 0; i < Math.min(parameters.length, args.length); i++) {
                    context.setVariable(parameters[i].getName(), args[i]);
                }
            }
        } catch (Exception e) {
            log.debug("{} Could not extract parameter names", LOG_NAME, e);
        }
    }


    /**
     * Adds the current user ID to the evaluation context as 'user' and 'userId'.
     */
    private void addUserContextVariables(StandardEvaluationContext context) {
        String userId = getCurrentUserId();
        if (userId != null) {
            context.setVariable("userId", userId);
            context.setVariable("user", userId);
        }
    }

    /**
     * Adds the requester's IP address to the evaluation context as 'ip' and 'ipAddress'.
     */
    private void addRequestContextVariables(StandardEvaluationContext context) {
        String ipAddress = getCurrentIpAddress();
        if (ipAddress != null) {
            context.setVariable("ipAddress", ipAddress);
            context.setVariable("ip", ipAddress);
        }
    }

    /**
     * Retrieves the current user ID using the provided {@link UserResourceManagementProvider}.
     *
     * @return the user ID or null if unavailable
     */
    protected @Nullable String getCurrentUserId() {
        try {
            return userResourceManagementProvider.getCurrentUserId();
        } catch (Exception e) {
            log.debug("{} Could not get current user", LOG_NAME, e);
            return null;
        }
    }

    /**
     * Resolves the IP address of the incoming request.
     *
     * @return the client IP address, or null if not available
     */
    protected @Nullable String getCurrentIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // Check for X-Forwarded-For header first
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    // Take the first IP in case of multiple
                    return xForwardedFor.split(",")[0].trim();
                }
                
                // Check for X-Real-IP header
                String xRealIp = request.getHeader("X-Real-IP");
                if (xRealIp != null && !xRealIp.isEmpty()) {
                    return xRealIp;
                }
                
                // Fall back to remote address
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.debug("{} Could not get current IP address", LOG_NAME, e);
        }
        return null;
    }

    /**
     * Generates a hashed key based on method arguments to avoid long key strings.
     * Uses MD5 to convert argument values into a consistent short string.
     *
     * @param args the method arguments
     * @return an MD5 hash string representing the argument combination
     */
    protected @NonNull String generateParametersKey(Object[] args) {
        if (args == null || args.length == 0) {
            return "noargs";
        }
        
        try {
            StringBuilder sb = new StringBuilder();
            for (Object arg : args) {
                if (arg != null) {
                    if (arg.getClass().isArray()) {
                        sb.append(Arrays.deepToString((Object[]) arg));
                    } else {
                        sb.append(arg);
                    }
                } else {
                    sb.append("null");
                }
                sb.append("|");
            }
            
            // Generate MD5 hash to keep key length manageable
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(sb.toString().getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            log.warn("{} Failed to generate parameters key", LOG_NAME, e);
            return "params";
        }
    }

    /**
     * Constructs a fallback default key using class and method name.
     *
     * @param joinPoint join point of the intercepted method
     * @return a string in the format ClassName:methodName
     */
    protected String createDefaultKey(ProceedingJoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getSimpleName() + ":" + joinPoint.getSignature().getName();
    }
}