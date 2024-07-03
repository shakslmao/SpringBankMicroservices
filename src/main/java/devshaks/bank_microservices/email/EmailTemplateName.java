package devshaks.bank_microservices.email;

import lombok.Getter;

/**
 * Enum representing the names of email templates.
 */
@Getter
public enum EmailTemplateName {

    // Enum constant for the "activate_user_account" email template
    ACTIVATE_USER_ACCOUNT("activate_user_account");

    // Name of the email template
    private final String name;

    /**
     * Constructor to set the name of the email template.
     *
     * @param name The name of the email template
     */
    EmailTemplateName(String name) {
        this.name = name;
    }
}
