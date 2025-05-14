package org.example.advertisingagency.exception;

/**
 * Кидається, коли спроба видалити сутність порушує цілісність даних
 * (наприклад, існують зовнішні посилання або сутність бере участь у процесах).
 */
public class EntityInUseException extends RuntimeException {
    public EntityInUseException(String message) {
        super(message);
    }
}
