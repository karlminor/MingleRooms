package Client.gui;

// Persistent prompt text field
// Class from http://stackoverflow.com/questions/25125563/clear-prompt-text-in-javafx-textfield-only-when-user-starts-typing

import javafx.scene.control.TextField;

public class PPTextField extends TextField {
    public PPTextField(String prompt) {
        super("");
        setPromptText(prompt);
        getStyleClass().add("persistent-prompt");
        refreshPromptVisibility();

        textProperty().addListener(observable -> refreshPromptVisibility());
    }

    private void refreshPromptVisibility() {
        final String text = getText();
        if (isEmptyString(text)) {
            getStyleClass().remove("no-prompt");
        } else {
            if (!getStyleClass().contains("no-prompt")) {
                getStyleClass().add("no-prompt");
            }
        }
    }

    private boolean isEmptyString(String text) {
        return text == null || text.isEmpty();
    }
}

