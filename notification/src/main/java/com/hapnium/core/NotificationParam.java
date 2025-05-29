package com.hapnium.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>NotificationParam</h2>
 * Represents the service account credentials required to authenticate with
 * cloud messaging platforms like Firebase Cloud Messaging (FCM).
 * <p>
 * This data is typically derived from a Firebase service account JSON file.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class NotificationParam {
    /**
     * The type of credential (typically "service_account").
     */
    private String type;

    /**
     * The Firebase project ID.
     */
    private String project_id;

    /**
     * The ID of the private key used to sign requests.
     */
    private String private_key_id;

    /**
     * The private key used to authenticate and sign JWTs.
     */
    private String private_key;

    /**
     * The email address of the client or service account.
     */
    private String client_email;

    /**
     * The ID of the client associated with the credentials.
     */
    private String client_id;

    /**
     * The OAuth2 authentication URI.
     */
    private String auth_uri;

    /**
     * The OAuth2 token URI used to exchange credentials for access tokens.
     */
    private String token_uri;

    /**
     * The URL for the X.509 certificate of the auth provider.
     */
    private String auth_provider_x509_cert_url;

    /**
     * The URL for the X.509 certificate of the client.
     */
    private String client_x509_cert_url;

    /**
     * The universe domain, used for determining cloud endpoints.
     */
    private String universe_domain;
}